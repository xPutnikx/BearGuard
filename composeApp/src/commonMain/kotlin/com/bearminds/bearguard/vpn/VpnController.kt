package com.bearminds.bearguard.vpn

import kotlinx.coroutines.flow.StateFlow

/**
 * Controller interface for managing the VPN service.
 * Platform-specific implementations handle the actual VPN lifecycle.
 */
interface VpnController {

    /**
     * Current VPN running state.
     */
    val isRunning: StateFlow<Boolean>

    /**
     * Whether VPN permission is required before starting.
     * On Android, user needs to grant VPN permission.
     * On other platforms, this may always be false.
     */
    val requiresPermission: Boolean

    /**
     * Start the VPN service.
     * May require permission grant first (check requiresPermission).
     */
    fun start()

    /**
     * Stop the VPN service.
     */
    fun stop()

    /**
     * Toggle VPN state.
     */
    fun toggle() {
        if (isRunning.value) stop() else start()
    }
}
