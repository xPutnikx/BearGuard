package com.bearminds.bearguard.rules.ui

import com.bearminds.architecture.BaseViewModel
import com.bearminds.bearguard.rules.data.AppListProvider
import com.bearminds.bearguard.rules.data.RulesRepository
import com.bearminds.bearguard.rules.model.Rule
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope

class AppListViewModel(
    private val appListProvider: AppListProvider,
    private val rulesRepository: RulesRepository,
) : BaseViewModel<AppListContract.Event, AppListContract.State>() {

    override val initialState = AppListContract.State()

    init {
        onEvent(AppListContract.Event.LoadApps)
    }

    override fun handleEvent(event: AppListContract.Event) {
        when (event) {
            is AppListContract.Event.LoadApps -> loadApps()
            is AppListContract.Event.ToggleAppAccess -> toggleAppAccess(event.packageName, event.isAllowed)
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
                AppWithRule(
                    app = app,
                    isAllowed = rulesMap[app.packageName]?.isAllowed ?: true,
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
            val rule = Rule(
                packageName = packageName,
                isAllowed = isAllowed,
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
