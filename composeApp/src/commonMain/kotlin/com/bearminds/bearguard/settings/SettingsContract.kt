package com.bearminds.bearguard.settings

import com.bearminds.architecture.BaseViewModel.ViewEvent
import com.bearminds.architecture.BaseViewModel.ViewState

/**
 * MVI Contract for the Settings screen.
 */
object SettingsContract {

    data class State(
        val themeMode: ThemeMode = ThemeMode.SYSTEM,
        val defaultRuleForNewApps: DefaultRule = DefaultRule.ALLOW,
        val showSystemAppsByDefault: Boolean = false,
        val lockdownMode: Boolean = false,
        val autoStartOnBoot: Boolean = false,
    ) : ViewState

    sealed interface Event : ViewEvent {
        data class SetThemeMode(val mode: ThemeMode) : Event
        data class SetDefaultRuleForNewApps(val rule: DefaultRule) : Event
        data class SetShowSystemAppsByDefault(val show: Boolean) : Event
        data class SetLockdownMode(val enabled: Boolean) : Event
        data class SetAutoStartOnBoot(val enabled: Boolean) : Event
    }

    enum class ThemeMode {
        LIGHT,
        DARK,
        SYSTEM
    }

    enum class DefaultRule {
        ALLOW,
        BLOCK
    }
}
