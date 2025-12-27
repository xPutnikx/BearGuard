package com.bearminds.bearguard.network

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * iOS stub implementation of [NetworkTypeProvider].
 * TODO: Implement using NWPathMonitor from Network framework.
 */
class IosNetworkTypeProvider : NetworkTypeProvider {

    private val _currentNetworkType = MutableStateFlow(NetworkType.WIFI)
    override val currentNetworkType: StateFlow<NetworkType> = _currentNetworkType.asStateFlow()
}
