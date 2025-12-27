package com.bearminds.bearguard

import android.app.Application
import com.bearminds.bearguard.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class BearGuardApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@BearGuardApplication)
            modules(appModules())
        }
    }
}
