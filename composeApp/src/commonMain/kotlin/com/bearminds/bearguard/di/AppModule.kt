package com.bearminds.bearguard.di

import org.koin.core.module.Module

/**
 * All app modules combined.
 */
fun appModules(): List<Module> = listOf(
    platformModule,
    rulesModule,
    homeModule,
    trafficModule,
)
