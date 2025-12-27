package com.bearminds.bearguard.network

import kotlinx.coroutines.flow.StateFlow

/**
 * Represents the current network connection type.
 */
enum class NetworkType {
    /** Connected via WiFi */
    WIFI,
    /** Connected via mobile/cellular data */
    MOBILE,
    /** No network connection */
    NONE
}

/**
 * Provider for observing the current network connection type.
 * Platform-specific implementations use system APIs to detect network changes.
 */
interface NetworkTypeProvider {

    /**
     * Current network type as a StateFlow for reactive observation.
     * Updates automatically when network type changes.
     */
    val currentNetworkType: StateFlow<NetworkType>
}
