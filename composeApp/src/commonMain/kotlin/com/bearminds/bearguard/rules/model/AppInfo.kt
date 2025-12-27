package com.bearminds.bearguard.rules.model

/**
 * Represents an installed application on the device.
 *
 * @param packageName Unique package identifier (e.g., "com.google.chrome")
 * @param name Display name of the app
 * @param isSystemApp Whether this is a system/pre-installed app
 * @param uid Unique user ID assigned by Android for network traffic identification
 * @param hasInternetPermission Whether the app has INTERNET permission
 * @param isEnabled Whether the app is enabled (not disabled by user)
 */
data class AppInfo(
    val packageName: String,
    val name: String,
    val isSystemApp: Boolean,
    val uid: Int,
    val hasInternetPermission: Boolean = true,
    val isEnabled: Boolean = true,
)
