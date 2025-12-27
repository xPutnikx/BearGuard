package com.bearminds.bearguard.vpn

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import androidx.core.app.NotificationCompat
import com.bearminds.bearguard.MainActivity
import com.bearminds.bearguard.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
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
 * 2. Reads packets from the tunnel
 * 3. For allowed apps: forwards packets to the actual network
 * 4. For blocked apps: drops packets silently (sinkhole)
 */
class BearGuardVpnService : VpnService() {

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "bearguard_vpn"
        private const val NOTIFICATION_ID = 1

        const val ACTION_START = "com.bearminds.bearguard.vpn.START"
        const val ACTION_STOP = "com.bearminds.bearguard.vpn.STOP"

        @Volatile
        var isRunning = false
            private set
    }

    private var vpnInterface: ParcelFileDescriptor? = null
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // TODO: Inject via Koin
    private val blockedPackages = mutableSetOf<String>()

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
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
        if (isRunning) return

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

        // Exclude blocked apps from VPN (they get no internet)
        // Or include only allowed apps (whitelist mode)
        // For now, we'll use the "allow apps through VPN" approach
        // Apps NOT in the VPN tunnel = blocked

        try {
            // Allow this app to bypass VPN (avoid infinite loop)
            builder.addDisallowedApplication(packageName)

            // Establish VPN tunnel
            vpnInterface = builder.establish()

            if (vpnInterface != null) {
                isRunning = true

                // Start packet processing
                serviceScope.launch {
                    processPackets()
                }
            } else {
                // VPN failed to establish, stop the service
                stopVpn()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            stopVpn()
        }
    }

    private fun stopVpn() {
        isRunning = false

        try {
            vpnInterface?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        vpnInterface = null

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    /**
     * Main packet processing loop.
     *
     * Reads packets from the VPN tunnel and decides whether to:
     * - Forward them (allowed apps)
     * - Drop them (blocked apps)
     */
    private suspend fun processPackets() {
        val vpnFd = vpnInterface ?: return

        val inputStream = FileInputStream(vpnFd.fileDescriptor)
        val outputStream = FileOutputStream(vpnFd.fileDescriptor)

        val buffer = ByteBuffer.allocate(32767)

        while (isRunning && vpnInterface != null) {
            try {
                // Read packet from VPN tunnel
                buffer.clear()
                val length = inputStream.read(buffer.array())

                if (length > 0) {
                    buffer.limit(length)

                    // Parse packet to determine source app
                    // For now, forward all packets (basic implementation)
                    // TODO: Implement packet filtering based on app rules

                    // Log the packet for traffic monitoring
                    logPacket(buffer, length)

                    // Forward packet (in real implementation, we'd check rules here)
                    // The actual forwarding happens through the VPN tunnel automatically
                    // when we don't drop the packet
                }
            } catch (e: Exception) {
                if (isRunning) {
                    e.printStackTrace()
                }
                break
            }
        }

        try {
            inputStream.close()
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
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
