package com.bearminds.bearguard.rules.ui

import com.bearminds.architecture.BaseViewModel
import com.bearminds.bearguard.rules.data.AppListProvider
import com.bearminds.bearguard.rules.data.RulesRepository
import com.bearminds.bearguard.rules.model.Rule
import com.bearminds.bearguard.settings.data.SettingsRepository
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope

class AppListViewModel(
    private val appListProvider: AppListProvider,
    private val rulesRepository: RulesRepository,
    private val settingsRepository: SettingsRepository,
) : BaseViewModel<AppListContract.Event, AppListContract.State>() {

    override val initialState = AppListContract.State()

    init {
        loadInitialSettings()
    }

    private fun loadInitialSettings() {
        viewModelScope.launch {
            val showSystemApps = settingsRepository.getShowSystemAppsByDefault()
            setState { copy(showSystemApps = showSystemApps) }
            onEvent(AppListContract.Event.LoadApps)
        }
    }

    override fun handleEvent(event: AppListContract.Event) {
        when (event) {
            is AppListContract.Event.LoadApps -> loadApps()
            is AppListContract.Event.ToggleAppAccess -> toggleAppAccess(event.packageName, event.isAllowed)
            is AppListContract.Event.ToggleWifiAccess -> toggleWifiAccess(event.packageName, event.allowWifi)
            is AppListContract.Event.ToggleMobileAccess -> toggleMobileAccess(event.packageName, event.allowMobile)
            is AppListContract.Event.ToggleScreenOffAccess -> toggleScreenOffAccess(event.packageName, event.allowWhenScreenOff)
            is AppListContract.Event.ToggleSystemApps -> toggleSystemApps(event.show)
            is AppListContract.Event.ToggleBlockedOnly -> toggleBlockedOnly(event.show)
            is AppListContract.Event.UpdateSearchQuery -> updateSearchQuery(event.query)
        }
    }

    private fun loadApps() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }

            val apps = appListProvider.getInstalledApps(includeSystemApps = viewState.value.showSystemApps)
            val rules = rulesRepository.getRules()
            val rulesMap = rules.associateBy { it.packageName }

            val appsWithRules = apps.map { app ->
                val rule = rulesMap[app.packageName]
                AppWithRule(
                    app = app,
                    isAllowed = rule?.isAllowed ?: true,
                    allowWifi = rule?.allowWifi ?: true,
                    allowMobile = rule?.allowMobileData ?: true,
                    allowWhenScreenOff = rule?.allowWhenScreenOff ?: true,
                )
            }

            setState {
                copy(
                    isLoading = false,
                    apps = appsWithRules,
                )
            }
        }
    }

    private fun toggleAppAccess(packageName: String, isAllowed: Boolean) {
        viewModelScope.launch {
            // Get existing rule to preserve other settings
            val existingRule = rulesRepository.getRule(packageName)
            val rule = Rule(
                packageName = packageName,
                isAllowed = isAllowed,
                allowWifi = existingRule?.allowWifi ?: true,
                allowMobileData = existingRule?.allowMobileData ?: true,
                allowWhenScreenOff = existingRule?.allowWhenScreenOff ?: true,
            )
            rulesRepository.saveRule(rule)

            // Update local state
            setState {
                copy(
                    apps = apps.map { appWithRule ->
                        if (appWithRule.app.packageName == packageName) {
                            appWithRule.copy(isAllowed = isAllowed)
                        } else {
                            appWithRule
                        }
                    }
                )
            }
        }
    }

    private fun toggleWifiAccess(packageName: String, allowWifi: Boolean) {
        viewModelScope.launch {
            // Get existing rule to preserve other settings
            val existingRule = rulesRepository.getRule(packageName)
            val rule = Rule(
                packageName = packageName,
                isAllowed = existingRule?.isAllowed ?: true,
                allowWifi = allowWifi,
                allowMobileData = existingRule?.allowMobileData ?: true,
                allowWhenScreenOff = existingRule?.allowWhenScreenOff ?: true,
            )
            rulesRepository.saveRule(rule)

            // Update local state
            setState {
                copy(
                    apps = apps.map { appWithRule ->
                        if (appWithRule.app.packageName == packageName) {
                            appWithRule.copy(allowWifi = allowWifi)
                        } else {
                            appWithRule
                        }
                    }
                )
            }
        }
    }

    private fun toggleMobileAccess(packageName: String, allowMobile: Boolean) {
        viewModelScope.launch {
            // Get existing rule to preserve other settings
            val existingRule = rulesRepository.getRule(packageName)
            val rule = Rule(
                packageName = packageName,
                isAllowed = existingRule?.isAllowed ?: true,
                allowWifi = existingRule?.allowWifi ?: true,
                allowMobileData = allowMobile,
                allowWhenScreenOff = existingRule?.allowWhenScreenOff ?: true,
            )
            rulesRepository.saveRule(rule)

            // Update local state
            setState {
                copy(
                    apps = apps.map { appWithRule ->
                        if (appWithRule.app.packageName == packageName) {
                            appWithRule.copy(allowMobile = allowMobile)
                        } else {
                            appWithRule
                        }
                    }
                )
            }
        }
    }

    private fun toggleScreenOffAccess(packageName: String, allowWhenScreenOff: Boolean) {
        viewModelScope.launch {
            // Get existing rule to preserve other settings
            val existingRule = rulesRepository.getRule(packageName)
            val rule = Rule(
                packageName = packageName,
                isAllowed = existingRule?.isAllowed ?: true,
                allowWifi = existingRule?.allowWifi ?: true,
                allowMobileData = existingRule?.allowMobileData ?: true,
                allowWhenScreenOff = allowWhenScreenOff,
            )
            rulesRepository.saveRule(rule)

            // Update local state
            setState {
                copy(
                    apps = apps.map { appWithRule ->
                        if (appWithRule.app.packageName == packageName) {
                            appWithRule.copy(allowWhenScreenOff = allowWhenScreenOff)
                        } else {
                            appWithRule
                        }
                    }
                )
            }
        }
    }

    private fun toggleSystemApps(show: Boolean) {
        setState { copy(showSystemApps = show) }
        onEvent(AppListContract.Event.LoadApps)
    }

    private fun toggleBlockedOnly(show: Boolean) {
        setState { copy(showBlockedOnly = show) }
    }

    private fun updateSearchQuery(query: String) {
        setState { copy(searchQuery = query) }
    }
}
