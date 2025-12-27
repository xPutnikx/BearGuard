package com.bearminds.bearguard.screen

import kotlinx.coroutines.flow.StateFlow

/**
 * Provider for observing the screen on/off state.
 * Platform-specific implementations use system APIs to detect screen state changes.
 */
interface ScreenStateProvider {

    /**
     * Whether the screen is currently on.
     * Updates automatically when screen state changes.
     */
    val isScreenOn: StateFlow<Boolean>
}
