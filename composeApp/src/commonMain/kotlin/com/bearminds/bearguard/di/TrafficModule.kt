package com.bearminds.bearguard.di

import com.bearminds.bearguard.traffic.TrafficViewModel
import com.bearminds.bearguard.traffic.data.TrafficRepository
import com.bearminds.bearguard.traffic.data.TrafficRepositoryImpl
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val trafficModule = module {
    single<TrafficRepository> { TrafficRepositoryImpl() }
    viewModel { TrafficViewModel(get()) }
}
