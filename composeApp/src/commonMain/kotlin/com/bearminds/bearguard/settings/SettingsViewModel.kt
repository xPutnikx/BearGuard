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
            settingsRepository.observeLockdownMode(),
            settingsRepository.observeAutoStartOnBoot(),
        ) { themeMode, defaultRule, showSystemApps, lockdownMode, autoStart ->
            SettingsState(themeMode, defaultRule, showSystemApps, lockdownMode, autoStart)
        }
            .onEach { settings ->
                setState {
                    copy(
                        themeMode = settings.themeMode,
                        defaultRuleForNewApps = settings.defaultRule,
                        showSystemAppsByDefault = settings.showSystemApps,
                        lockdownMode = settings.lockdownMode,
                        autoStartOnBoot = settings.autoStartOnBoot,
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private data class SettingsState(
        val themeMode: ThemeMode,
        val defaultRule: DefaultRule,
        val showSystemApps: Boolean,
        val lockdownMode: Boolean,
        val autoStartOnBoot: Boolean,
    )

    override fun handleEvent(event: SettingsContract.Event) {
        when (event) {
            is SettingsContract.Event.SetThemeMode -> setThemeMode(event.mode)
            is SettingsContract.Event.SetDefaultRuleForNewApps -> setDefaultRuleForNewApps(event.rule)
            is SettingsContract.Event.SetShowSystemAppsByDefault -> setShowSystemAppsByDefault(event.show)
            is SettingsContract.Event.SetLockdownMode -> setLockdownMode(event.enabled)
            is SettingsContract.Event.SetAutoStartOnBoot -> setAutoStartOnBoot(event.enabled)
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

    private fun setLockdownMode(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setLockdownMode(enabled)
        }
    }

    private fun setAutoStartOnBoot(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setAutoStartOnBoot(enabled)
        }
    }
}
