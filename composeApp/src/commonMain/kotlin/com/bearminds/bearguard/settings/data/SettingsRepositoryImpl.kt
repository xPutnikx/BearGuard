package com.bearminds.bearguard.settings.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.bearminds.bearguard.settings.SettingsContract.DefaultRule
import com.bearminds.bearguard.settings.SettingsContract.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * DataStore-based implementation of [SettingsRepository].
 */
class SettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepository {

    companion object {
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        private val DEFAULT_RULE_KEY = stringPreferencesKey("default_rule_for_new_apps")
        private val SHOW_SYSTEM_APPS_KEY = booleanPreferencesKey("show_system_apps_by_default")
    }

    override fun observeThemeMode(): Flow<ThemeMode> {
        return dataStore.data.map { preferences ->
            preferences[THEME_MODE_KEY]?.let { value ->
                ThemeMode.entries.find { it.name == value }
            } ?: ThemeMode.SYSTEM
        }
    }

    override suspend fun getThemeMode(): ThemeMode {
        return observeThemeMode().first()
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = mode.name
        }
    }

    override fun observeDefaultRuleForNewApps(): Flow<DefaultRule> {
        return dataStore.data.map { preferences ->
            preferences[DEFAULT_RULE_KEY]?.let { value ->
                DefaultRule.entries.find { it.name == value }
            } ?: DefaultRule.ALLOW
        }
    }

    override suspend fun getDefaultRuleForNewApps(): DefaultRule {
        return observeDefaultRuleForNewApps().first()
    }

    override suspend fun setDefaultRuleForNewApps(rule: DefaultRule) {
        dataStore.edit { preferences ->
            preferences[DEFAULT_RULE_KEY] = rule.name
        }
    }

    override fun observeShowSystemAppsByDefault(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[SHOW_SYSTEM_APPS_KEY] ?: false
        }
    }

    override suspend fun getShowSystemAppsByDefault(): Boolean {
        return observeShowSystemAppsByDefault().first()
    }

    override suspend fun setShowSystemAppsByDefault(show: Boolean) {
        dataStore.edit { preferences ->
            preferences[SHOW_SYSTEM_APPS_KEY] = show
        }
    }
}
