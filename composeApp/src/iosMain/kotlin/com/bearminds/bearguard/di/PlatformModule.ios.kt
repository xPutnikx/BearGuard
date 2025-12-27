package com.bearminds.bearguard.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.bearminds.bearguard.network.IosNetworkTypeProvider
import com.bearminds.bearguard.network.NetworkTypeProvider
import com.bearminds.bearguard.screen.IosScreenStateProvider
import com.bearminds.bearguard.screen.ScreenStateProvider
import com.bearminds.bearguard.vpn.IosVpnController
import com.bearminds.bearguard.vpn.VpnController
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
actual val platformModule = module {
    single<DataStore<Preferences>> {
        DataStoreFactory.create {
            val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
                directory = NSDocumentDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = false,
                error = null,
            )
            requireNotNull(documentDirectory).path + "/bearguard_prefs.preferences_pb"
        }
    }

    single<VpnController> { IosVpnController() }
    single<NetworkTypeProvider> { IosNetworkTypeProvider() }
    single<ScreenStateProvider> { IosScreenStateProvider() }
}
