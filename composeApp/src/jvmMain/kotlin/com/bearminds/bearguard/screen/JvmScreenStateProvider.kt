package com.bearminds.bearguard.screen

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * JVM stub implementation of [ScreenStateProvider].
 * Desktop always reports screen on (no sleep-based blocking).
 */
class JvmScreenStateProvider : ScreenStateProvider {

    private val _isScreenOn = MutableStateFlow(true)
    override val isScreenOn: StateFlow<Boolean> = _isScreenOn.asStateFlow()
}
