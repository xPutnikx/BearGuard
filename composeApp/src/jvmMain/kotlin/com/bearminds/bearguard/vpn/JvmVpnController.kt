package com.bearminds.bearguard.vpn

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * JVM/Desktop stub implementation of [VpnController].
 * VPN functionality is not available on desktop.
 */
class JvmVpnController : VpnController {

    private val _isRunning = MutableStateFlow(false)
    override val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    override val requiresPermission: Boolean = false

    override fun start() {
        // No-op on desktop
        _isRunning.value = true
    }

    override fun stop() {
        // No-op on desktop
        _isRunning.value = false
    }
}
