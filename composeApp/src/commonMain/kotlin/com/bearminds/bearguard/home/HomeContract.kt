package com.bearminds.bearguard.home

import com.bearminds.architecture.BaseViewModel.ViewEvent
import com.bearminds.architecture.BaseViewModel.ViewState

/**
 * MVI Contract for the Home screen (VPN control).
 */
object HomeContract {

    data class State(
        val isVpnRunning: Boolean = false,
        val isLoading: Boolean = false,
    ) : ViewState

    sealed interface Event : ViewEvent {
        data object ToggleVpn : Event
        data object VpnStateChanged : Event
    }
}
