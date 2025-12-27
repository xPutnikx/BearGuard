package com.bearminds.bearguard.network

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * JVM stub implementation of [NetworkTypeProvider].
 * Desktop always reports WIFI (assuming wired/wireless LAN).
 */
class JvmNetworkTypeProvider : NetworkTypeProvider {

    private val _currentNetworkType = MutableStateFlow(NetworkType.WIFI)
    override val currentNetworkType: StateFlow<NetworkType> = _currentNetworkType.asStateFlow()
}
