package com.bearminds.bearguard.settings.data

import com.bearminds.bearguard.settings.SettingsContract.DefaultRule
import com.bearminds.bearguard.settings.SettingsContract.ThemeMode
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing app settings.
 */
interface SettingsRepository {

    /**
     * Observe the current theme mode setting.
     */
    fun observeThemeMode(): Flow<ThemeMode>

    /**
     * Get the current theme mode.
     */
    suspend fun getThemeMode(): ThemeMode

    /**
     * Set the theme mode.
     */
    suspend fun setThemeMode(mode: ThemeMode)

    /**
     * Observe the default rule for new apps setting.
     */
    fun observeDefaultRuleForNewApps(): Flow<DefaultRule>

    /**
     * Get the default rule for new apps.
     */
    suspend fun getDefaultRuleForNewApps(): DefaultRule

    /**
     * Set the default rule for new apps.
     */
    suspend fun setDefaultRuleForNewApps(rule: DefaultRule)

    /**
     * Observe the show system apps by default setting.
     */
    fun observeShowSystemAppsByDefault(): Flow<Boolean>

    /**
     * Get whether system apps should be shown by default.
     */
    suspend fun getShowSystemAppsByDefault(): Boolean

    /**
     * Set whether system apps should be shown by default.
     */
    suspend fun setShowSystemAppsByDefault(show: Boolean)

    /**
     * Observe the lockdown mode setting.
     * When enabled, only apps with explicit "allow" rules can connect.
     */
    fun observeLockdownMode(): Flow<Boolean>

    /**
     * Get whether lockdown mode is enabled.
     */
    suspend fun getLockdownMode(): Boolean

    /**
     * Set lockdown mode.
     */
    suspend fun setLockdownMode(enabled: Boolean)
}
