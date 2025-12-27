package com.bearminds.bearguard.screen

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.PowerManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Android implementation of [ScreenStateProvider] using BroadcastReceiver.
 * Listens for ACTION_SCREEN_ON and ACTION_SCREEN_OFF broadcasts.
 */
class AndroidScreenStateProvider(
    private val context: Context,
) : ScreenStateProvider {

    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager

    private val _isScreenOn = MutableStateFlow(getCurrentScreenState())
    override val isScreenOn: StateFlow<Boolean> = _isScreenOn.asStateFlow()

    private val screenStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Intent.ACTION_SCREEN_ON -> _isScreenOn.value = true
                Intent.ACTION_SCREEN_OFF -> _isScreenOn.value = false
            }
        }
    }

    init {
        // Register for screen state changes
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(screenStateReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            context.registerReceiver(screenStateReceiver, filter)
        }
    }

    /**
     * Get the current screen state by querying the system.
     */
    private fun getCurrentScreenState(): Boolean {
        return powerManager.isInteractive
    }
}
