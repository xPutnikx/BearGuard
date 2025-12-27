package com.bearminds.bearguard.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.bearminds.bearguard.network.AndroidNetworkTypeProvider
import com.bearminds.bearguard.network.NetworkTypeProvider
import com.bearminds.bearguard.rules.AndroidAppListProvider
import com.bearminds.bearguard.rules.data.AppListProvider
import com.bearminds.bearguard.screen.AndroidScreenStateProvider
import com.bearminds.bearguard.screen.ScreenStateProvider
import com.bearminds.bearguard.vpn.AndroidVpnController
import com.bearminds.bearguard.vpn.VpnController
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformModule = module {
    single<DataStore<Preferences>> {
        DataStoreFactory.create {
            androidContext().filesDir.resolve("bearguard_prefs.preferences_pb").absolutePath
        }
    }

    single<AppListProvider> { AndroidAppListProvider(androidContext()) }
    single<VpnController> { AndroidVpnController(androidContext()) }
    single<NetworkTypeProvider> { AndroidNetworkTypeProvider(androidContext()) }
    single<ScreenStateProvider> { AndroidScreenStateProvider(androidContext()) }
}
