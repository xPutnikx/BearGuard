package com.bearminds.bearguard.vpn

import android.content.Context
import android.content.Intent
import android.net.VpnService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Android implementation of [VpnController].
 * Manages the BearGuardVpnService lifecycle.
 */
class AndroidVpnController(
    private val context: Context,
) : VpnController {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _isRunning = MutableStateFlow(false)
    override val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    override val requiresPermission: Boolean = true

    init {
        // Poll VPN service state periodically
        scope.launch {
            while (true) {
                _isRunning.value = BearGuardVpnService.isRunning
                delay(1000)
            }
        }
    }

    /**
     * Check if VPN permission is granted.
     * Returns the prepare intent if permission needed, null if already granted.
     */
    fun prepareVpn(): Intent? {
        return VpnService.prepare(context)
    }

    override fun start() {
        val intent = Intent(context, BearGuardVpnService::class.java).apply {
            action = BearGuardVpnService.ACTION_START
        }
        context.startForegroundService(intent)
    }

    override fun stop() {
        val intent = Intent(context, BearGuardVpnService::class.java).apply {
            action = BearGuardVpnService.ACTION_STOP
        }
        context.startService(intent)
    }
}
