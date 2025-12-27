package com.bearminds.bearguard.vpn

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * iOS stub implementation of [VpnController].
 * TODO: Implement using NetworkExtension framework.
 */
class IosVpnController : VpnController {

    private val _isRunning = MutableStateFlow(false)
    override val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    override val requiresPermission: Boolean = true

    override fun start() {
        // TODO: Implement with NetworkExtension
        _isRunning.value = true
    }

    override fun stop() {
        // TODO: Implement with NetworkExtension
        _isRunning.value = false
    }
}
