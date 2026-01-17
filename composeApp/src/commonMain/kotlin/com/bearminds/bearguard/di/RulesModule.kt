package com.bearminds.bearguard.di

import com.bearminds.bearguard.rules.data.RulesRepository
import com.bearminds.bearguard.rules.data.RulesRepositoryImpl
import com.bearminds.bearguard.rules.ui.AppListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val rulesModule = module {
    single<RulesRepository> { RulesRepositoryImpl(get()) }
    viewModel { AppListViewModel(get(), get(), get(), get()) }
}
