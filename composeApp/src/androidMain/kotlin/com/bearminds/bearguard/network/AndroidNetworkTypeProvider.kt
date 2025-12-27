package com.bearminds.bearguard.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Android implementation of [NetworkTypeProvider] using ConnectivityManager.
 * Registers a network callback to receive real-time network state changes.
 */
class AndroidNetworkTypeProvider(
    context: Context,
) : NetworkTypeProvider {

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _currentNetworkType = MutableStateFlow(getCurrentNetworkTypeFromSystem())
    override val currentNetworkType: StateFlow<NetworkType> = _currentNetworkType.asStateFlow()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
            _currentNetworkType.value = determineNetworkType(capabilities)
        }

        override fun onLost(network: Network) {
            // When network is lost, check if there's still an active network
            _currentNetworkType.value = getCurrentNetworkTypeFromSystem()
        }

        override fun onAvailable(network: Network) {
            // When a new network becomes available, update the type
            _currentNetworkType.value = getCurrentNetworkTypeFromSystem()
        }
    }

    init {
        // Register for network changes
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    /**
     * Get the current network type by querying the system.
     */
    private fun getCurrentNetworkTypeFromSystem(): NetworkType {
        val activeNetwork = connectivityManager.activeNetwork ?: return NetworkType.NONE
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return NetworkType.NONE
        return determineNetworkType(capabilities)
    }

    /**
     * Determine network type from capabilities.
     * WiFi takes priority over cellular if both are present.
     */
    private fun determineNetworkType(capabilities: NetworkCapabilities): NetworkType {
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkType.WIFI
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkType.MOBILE
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> NetworkType.WIFI // Treat ethernet as WiFi
            else -> NetworkType.NONE
        }
    }
}
