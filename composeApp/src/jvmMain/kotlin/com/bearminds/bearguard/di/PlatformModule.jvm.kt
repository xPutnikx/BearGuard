package com.bearminds.bearguard.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.bearminds.bearguard.network.JvmNetworkTypeProvider
import com.bearminds.bearguard.network.NetworkTypeProvider
import com.bearminds.bearguard.screen.JvmScreenStateProvider
import com.bearminds.bearguard.screen.ScreenStateProvider
import com.bearminds.bearguard.vpn.JvmVpnController
import com.bearminds.bearguard.vpn.VpnController
import org.koin.dsl.module
import java.io.File

actual val platformModule = module {
    single<DataStore<Preferences>> {
        DataStoreFactory.create {
            val dataDir = File(System.getProperty("user.home"), ".bearguard")
            dataDir.mkdirs()
            File(dataDir, "bearguard_prefs.preferences_pb").absolutePath
        }
    }

    single<VpnController> { JvmVpnController() }
    single<NetworkTypeProvider> { JvmNetworkTypeProvider() }
    single<ScreenStateProvider> { JvmScreenStateProvider() }
}
