package com.bearminds.bearguard.screen

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * iOS stub implementation of [ScreenStateProvider].
 * TODO: Implement using UIApplication notifications for screen state.
 */
class IosScreenStateProvider : ScreenStateProvider {

    private val _isScreenOn = MutableStateFlow(true)
    override val isScreenOn: StateFlow<Boolean> = _isScreenOn.asStateFlow()
}
