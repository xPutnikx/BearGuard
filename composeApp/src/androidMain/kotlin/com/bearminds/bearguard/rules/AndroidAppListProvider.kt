package com.bearminds.bearguard.rules

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log
import com.bearminds.bearguard.rules.data.AppListProvider
import com.bearminds.bearguard.rules.model.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Android implementation of [AppListProvider] using PackageManager.
 */
class AndroidAppListProvider(
    private val context: Context,
) : AppListProvider {

    companion object {
        private const val TAG = "AndroidAppListProvider"
    }

    override suspend fun getInstalledApps(includeSystemApps: Boolean): List<AppInfo> {
        return withContext(Dispatchers.IO) {
            val packageManager = context.packageManager

            // Get all installed packages
            val packages: List<PackageInfo> = packageManager.getInstalledPackages(0)
            Log.d(TAG, "Total installed packages: ${packages.size}")

            val result = packages
                .filter { info ->
                    // Exclude our own app
                    info.packageName != context.packageName
                }
                .mapNotNull { info ->
                    try {
                        val isSystem = isSystem(info)
                        val hasInternet = hasInternet(info.packageName, packageManager)
                        val isEnabled = isEnabled(info, packageManager)

                        AppInfo(
                            packageName = info.packageName,
                            name = info.applicationInfo?.let {
                                packageManager.getApplicationLabel(it).toString()
                            } ?: info.packageName,
                            isSystemApp = isSystem,
                            uid = info.applicationInfo?.uid ?: -1,
                            hasInternetPermission = hasInternet,
                            isEnabled = isEnabled,
                        )
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to load app info for ${info.packageName}", e)
                        null
                    }
                }
                .filter { app ->
                    // Filter by system app preference
                    includeSystemApps || !app.isSystemApp
                }
                .sortedBy { it.name.lowercase() }

            Log.d(TAG, "Returning ${result.size} apps (includeSystemApps=$includeSystemApps)")
            Log.d(TAG, "System apps: ${result.count { it.isSystemApp }}, User apps: ${result.count { !it.isSystemApp }}")
            result.take(10).forEach { app ->
                Log.d(TAG, "  - ${app.name} (${app.packageName}, system=${app.isSystemApp}, internet=${app.hasInternetPermission})")
            }

            result
        }
    }

    /**
     * Check if app is a system app.
     * Checks both FLAG_SYSTEM and FLAG_UPDATED_SYSTEM_APP.
     */
    private fun isSystem(info: PackageInfo): Boolean {
        val flags = info.applicationInfo?.flags ?: return false
        return (flags and (ApplicationInfo.FLAG_SYSTEM or ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) != 0
    }

    /**
     * Check if app has INTERNET permission.
     */
    private fun hasInternet(packageName: String, pm: PackageManager): Boolean {
        return pm.checkPermission(
            android.Manifest.permission.INTERNET,
            packageName
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Check if app is enabled.
     */
    private fun isEnabled(info: PackageInfo, pm: PackageManager): Boolean {
        val setting = try {
            pm.getApplicationEnabledSetting(info.packageName)
        } catch (e: IllegalArgumentException) {
            Log.w(TAG, "Error getting enabled setting for ${info.packageName}", e)
            PackageManager.COMPONENT_ENABLED_STATE_DEFAULT
        }

        return if (setting == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT) {
            info.applicationInfo?.enabled ?: true
        } else {
            setting == PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        }
    }
}
