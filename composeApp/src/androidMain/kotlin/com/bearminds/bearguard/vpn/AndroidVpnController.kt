package com.bearminds.bearguard.vpn

import android.content.Context
import android.content.Intent
import android.net.VpnService
import kotlinx.coroutines.flow.StateFlow

/**
 * Android implementation of [VpnController].
 * Manages the BearGuardVpnService lifecycle.
 */
class AndroidVpnController(
    private val context: Context,
) : VpnController {

    /**
     * Observe VPN running state directly from the service's StateFlow.
     * No polling needed - updates are immediate.
     */
    override val isRunning: StateFlow<Boolean> = BearGuardVpnService.isRunning

    override val requiresPermission: Boolean = true

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
