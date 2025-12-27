package com.bearminds.bearguard.rules.ui

import com.bearminds.architecture.BaseViewModel.ViewEvent
import com.bearminds.architecture.BaseViewModel.ViewState
import com.bearminds.bearguard.rules.model.AppInfo

/**
 * MVI Contract for the App List screen.
 */
object AppListContract {

    data class State(
        val isLoading: Boolean = true,
        val apps: List<AppWithRule> = emptyList(),
        val showSystemApps: Boolean = false,
        val showBlockedOnly: Boolean = false,
        val searchQuery: String = "",
    ) : ViewState

    sealed interface Event : ViewEvent {
        data object LoadApps : Event
        data class ToggleAppAccess(val packageName: String, val isAllowed: Boolean) : Event
        data class ToggleWifiAccess(val packageName: String, val allowWifi: Boolean) : Event
        data class ToggleMobileAccess(val packageName: String, val allowMobile: Boolean) : Event
        data class ToggleSystemApps(val show: Boolean) : Event
        data class ToggleBlockedOnly(val show: Boolean) : Event
        data class UpdateSearchQuery(val query: String) : Event
    }
}

/**
 * Combines app info with its current rule state.
 */
data class AppWithRule(
    val app: AppInfo,
    val isAllowed: Boolean,
    val allowWifi: Boolean = true,
    val allowMobile: Boolean = true,
)
