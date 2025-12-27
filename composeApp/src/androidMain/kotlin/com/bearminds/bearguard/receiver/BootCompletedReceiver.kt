package com.bearminds.bearguard.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.bearminds.bearguard.settings.data.SettingsRepository
import com.bearminds.bearguard.vpn.BearGuardVpnService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

/**
 * BroadcastReceiver that starts the VPN service on device boot
 * if auto-start is enabled in settings.
 */
class BootCompletedReceiver : BroadcastReceiver(), KoinComponent {

    private val receiverScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        receiverScope.launch {
            val settingsRepository: SettingsRepository = get()
            val autoStartEnabled = settingsRepository.getAutoStartOnBoot()

            if (autoStartEnabled) {
                startVpnService(context)
            }
        }
    }

    private fun startVpnService(context: Context) {
        val serviceIntent = Intent(context, BearGuardVpnService::class.java).apply {
            action = BearGuardVpnService.ACTION_START
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }
}
