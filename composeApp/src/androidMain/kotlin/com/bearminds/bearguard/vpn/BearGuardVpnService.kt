package com.bearminds.bearguard.vpn

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bearminds.bearguard.MainActivity
import com.bearminds.bearguard.rules.data.RulesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer

/**
 * BearGuard VPN Service
 *
 * Uses Android's VpnService API to intercept all network traffic.
 * Works as a local "sinkhole" - no external server involved.
 *
 * How it works:
 * 1. Creates a VPN tunnel that routes all device traffic through it
 * 2. Only apps explicitly allowed (isAllowed=true) are added to the VPN
 * 3. Apps NOT in the VPN tunnel have no internet access (sinkhole effect)
 */
class BearGuardVpnService : VpnService(), KoinComponent {

    companion object {
        private const val TAG = "BearGuardVpnService"
        private const val NOTIFICATION_CHANNEL_ID = "bearguard_vpn"
        private const val NOTIFICATION_ID = 1
        private const val RULE_CHANGE_DEBOUNCE_MS = 300L
        private const val VPN_RESTART_DELAY_MS = 100L

        const val ACTION_START = "com.bearminds.bearguard.vpn.START"
        const val ACTION_STOP = "com.bearminds.bearguard.vpn.STOP"

        private val _isRunning = kotlinx.coroutines.flow.MutableStateFlow(false)
        val isRunning: kotlinx.coroutines.flow.StateFlow<Boolean> = _isRunning
    }

    private lateinit var rulesRepository: RulesRepository

    private var vpnInterface: ParcelFileDescriptor? = null
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var currentBlockedPackages: Set<String> = emptySet()
    private var restartJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        rulesRepository = get()
        createNotificationChannel()
        observeRuleChanges()
    }

    /**
     * Observe rule changes and restart VPN when blocked apps change.
     * Uses debounce to prevent rapid restarts when multiple rules change quickly.
     */
    private fun observeRuleChanges() {
        serviceScope.launch {
            rulesRepository.observeRules()
                .debounce(RULE_CHANGE_DEBOUNCE_MS)
                .collect { rules ->
                    // Filter out BearGuard's package from blocked packages
                    val newBlockedPackages = rules
                        .filter { !it.isAllowed && it.packageName != packageName }
                        .map { it.packageName }
                        .toSet()

                    // Only restart if VPN is running and blocked packages changed
                    if (_isRunning.value && newBlockedPackages != currentBlockedPackages) {
                        // Cancel any pending restart before starting a new one
                        restartJob?.cancel()
                        restartJob = serviceScope.launch {
                            restartVpn()
                        }
                    }
                }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // MUST call startForeground immediately for ANY action to avoid ANR
        // when started via startForegroundService()
        startForeground(NOTIFICATION_ID, createNotification())

        return when (intent?.action) {
            ACTION_STOP -> {
                stopVpn()
                START_NOT_STICKY
            }
            ACTION_START, null -> {
                startVpn()
                START_STICKY
            }
            else -> {
                stopSelf()
                START_NOT_STICKY
            }
        }
    }

    override fun onDestroy() {
        stopVpn()
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun startVpn() {
        if (_isRunning.value) return

        // Load rules to determine which apps to block
        val rules = runBlocking { rulesRepository.getRules() }
        // Filter out BearGuard's own package to prevent accidental self-blocking
        val blockedPackages = rules
            .filter { !it.isAllowed && it.packageName != packageName }
            .map { it.packageName }
            .toSet()

        // Update current state for change detection
        currentBlockedPackages = blockedPackages

        // Build VPN interface
        val builder = Builder()
            .setSession("BearGuard")
            .addAddress("10.0.0.2", 32)           // VPN interface address
            .addRoute("0.0.0.0", 0)                // Route all IPv4 traffic
            .addRoute("::", 0)                      // Route all IPv6 traffic
            .addDnsServer("8.8.8.8")               // Google DNS
            .addDnsServer("8.8.4.4")
            .setMtu(1500)
            .setBlocking(true)

        try {
            // Always exclude BearGuard itself from VPN to maintain network access
            try {
                builder.addDisallowedApplication(packageName)
            } catch (e: Exception) {
                Log.w(TAG, "Failed to exclude self from VPN", e)
            }

            // Use addAllowedApplication for BLOCKED apps only
            // - Apps added via addAllowedApplication -> go through VPN -> packets dropped -> NO internet
            // - All other apps -> bypass VPN automatically -> normal internet
            for (blockedPackage in blockedPackages) {
                try {
                    builder.addAllowedApplication(blockedPackage)
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to add package: $blockedPackage", e)
                }
            }

            // Establish VPN tunnel
            vpnInterface = builder.establish()

            if (vpnInterface != null) {
                _isRunning.value = true

                // Start packet processing
                serviceScope.launch {
                    processPackets()
                }
            } else {
                Log.e(TAG, "VPN failed to establish - vpnInterface is null")
                stopVpn()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error starting VPN", e)
            stopVpn()
        }
    }

    private fun stopVpn() {
        _isRunning.value = false
        restartJob?.cancel()

        try {
            vpnInterface?.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error closing VPN interface", e)
        }
        vpnInterface = null

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    /**
     * Restart VPN to apply new rules.
     * Only restarts the VPN tunnel itself, keeps the service running.
     */
    private suspend fun restartVpn() {
        _isRunning.value = false

        try {
            vpnInterface?.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error closing VPN interface during restart", e)
        }
        vpnInterface = null

        // Small delay to ensure clean shutdown
        delay(VPN_RESTART_DELAY_MS)

        startVpn()
    }

    /**
     * Main packet processing loop.
     *
     * For now, we just read and discard packets since the blocking is done
     * at the VPN builder level (excluded apps can't send packets through the tunnel).
     */
    private suspend fun processPackets() {
        val vpnFd = vpnInterface ?: return

        val inputStream = FileInputStream(vpnFd.fileDescriptor)
        val outputStream = FileOutputStream(vpnFd.fileDescriptor)

        val buffer = ByteBuffer.allocate(32767)

        while (_isRunning.value && vpnInterface != null) {
            try {
                // Read packet from VPN tunnel
                buffer.clear()
                val length = inputStream.read(buffer.array())

                if (length > 0) {
                    buffer.limit(length)

                    // Log the packet for traffic monitoring
                    logPacket(buffer, length)

                    // Packets from allowed apps come through here
                    // We need to forward them to the actual network
                    // For now, the VPN tunnel handles this automatically
                }
            } catch (e: Exception) {
                if (_isRunning.value) {
                    Log.e(TAG, "Error processing packets", e)
                }
                break
            }
        }

        try {
            inputStream.close()
            outputStream.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error closing streams", e)
        }
    }

    /**
     * Log packet information for traffic monitoring.
     */
    private fun logPacket(buffer: ByteBuffer, length: Int) {
        // TODO: Parse IP header to extract:
        // - Source IP
        // - Destination IP
        // - Protocol (TCP/UDP)
        // - Source port
        // - Destination port
        //
        // Then map to app using Android's ConnectivityManager.getConnectionOwnerUid()
        // and log to traffic repository
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "BearGuard VPN",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows when BearGuard firewall is active"
                setShowBadge(false)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = PendingIntent.getService(
            this,
            0,
            Intent(this, BearGuardVpnService::class.java).apply {
                action = ACTION_STOP
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("BearGuard Active")
            .setContentText("Firewall is protecting your device")
            .setSmallIcon(android.R.drawable.ic_lock_lock) // TODO: Replace with app icon
            .setContentIntent(pendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop", stopIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
}
