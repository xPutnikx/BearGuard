package com.bearminds.bearguard.di

import com.bearminds.bearguard.settings.SettingsViewModel
import com.bearminds.bearguard.settings.data.SettingsRepository
import com.bearminds.bearguard.settings.data.SettingsRepositoryImpl
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    viewModel { SettingsViewModel(get()) }
}
