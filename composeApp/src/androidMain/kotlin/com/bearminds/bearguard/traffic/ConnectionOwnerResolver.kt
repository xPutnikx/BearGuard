package com.bearminds.bearguard.traffic

import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import java.net.InetSocketAddress

/**
 * Resolves connection owner (UID and package name) from network connection info.
 */
class ConnectionOwnerResolver(
    private val context: Context,
) {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val packageManager = context.packageManager

    // Cache UID to package name mappings
    private val uidToPackageCache = mutableMapOf<Int, String?>()

    /**
     * Get the UID that owns a connection based on local address/port and remote address/port.
     * Returns -1 if the owner cannot be determined.
     */
    fun getConnectionOwnerUid(
        protocol: Int, // IPPROTO_TCP (6) or IPPROTO_UDP (17)
        localAddress: String,
        localPort: Int,
        remoteAddress: String,
        remotePort: Int,
    ): Int {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return -1 // API not available before Android 10
        }

        return try {
            val local = InetSocketAddress(localAddress, localPort)
            val remote = InetSocketAddress(remoteAddress, remotePort)
            connectivityManager.getConnectionOwnerUid(protocol, local, remote)
        } catch (e: Exception) {
            -1
        }
    }

    /**
     * Get the package name for a given UID.
     * Returns null if the package cannot be determined.
     */
    fun getPackageNameForUid(uid: Int): String? {
        if (uid < 0) return null

        // Check cache first
        uidToPackageCache[uid]?.let { return it }

        val packageName = try {
            val packages = packageManager.getPackagesForUid(uid)
            packages?.firstOrNull()
        } catch (e: Exception) {
            null
        }

        // Cache the result
        uidToPackageCache[uid] = packageName
        return packageName
    }

    /**
     * Clear the UID-to-package cache.
     * Call this when apps are installed/uninstalled.
     */
    fun clearCache() {
        uidToPackageCache.clear()
    }

    companion object {
        const val IPPROTO_TCP = 6
        const val IPPROTO_UDP = 17
    }
}
