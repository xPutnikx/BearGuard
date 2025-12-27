package com.bearminds.bearguard.settings

import androidx.lifecycle.viewModelScope
import com.bearminds.architecture.BaseViewModel
import com.bearminds.bearguard.settings.SettingsContract.DefaultRule
import com.bearminds.bearguard.settings.SettingsContract.ThemeMode
import com.bearminds.bearguard.settings.data.SettingsRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
) : BaseViewModel<SettingsContract.Event, SettingsContract.State>() {

    override val initialState = SettingsContract.State()

    init {
        observeSettings()
    }

    private fun observeSettings() {
        combine(
            settingsRepository.observeThemeMode(),
            settingsRepository.observeDefaultRuleForNewApps(),
            settingsRepository.observeShowSystemAppsByDefault(),
        ) { themeMode, defaultRule, showSystemApps ->
            Triple(themeMode, defaultRule, showSystemApps)
        }
            .onEach { (themeMode, defaultRule, showSystemApps) ->
                setState {
                    copy(
                        themeMode = themeMode,
                        defaultRuleForNewApps = defaultRule,
                        showSystemAppsByDefault = showSystemApps,
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    override fun handleEvent(event: SettingsContract.Event) {
        when (event) {
            is SettingsContract.Event.SetThemeMode -> setThemeMode(event.mode)
            is SettingsContract.Event.SetDefaultRuleForNewApps -> setDefaultRuleForNewApps(event.rule)
            is SettingsContract.Event.SetShowSystemAppsByDefault -> setShowSystemAppsByDefault(event.show)
        }
    }

    private fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            settingsRepository.setThemeMode(mode)
        }
    }

    private fun setDefaultRuleForNewApps(rule: DefaultRule) {
        viewModelScope.launch {
            settingsRepository.setDefaultRuleForNewApps(rule)
        }
    }

    private fun setShowSystemAppsByDefault(show: Boolean) {
        viewModelScope.launch {
            settingsRepository.setShowSystemAppsByDefault(show)
        }
    }
}
