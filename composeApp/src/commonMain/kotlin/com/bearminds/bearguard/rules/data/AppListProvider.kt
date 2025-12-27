package com.bearminds.bearguard.rules.data

import com.bearminds.bearguard.rules.model.AppInfo

/**
 * Provider for getting the list of installed applications.
 */
interface AppListProvider {

    /**
     * Get all installed apps.
     *
     * @param includeSystemApps Whether to include system/pre-installed apps
     */
    suspend fun getInstalledApps(includeSystemApps: Boolean = false): List<AppInfo>
}
