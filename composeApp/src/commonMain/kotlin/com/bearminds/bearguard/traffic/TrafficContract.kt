package com.bearminds.bearguard.traffic

import com.bearminds.architecture.BaseViewModel.ViewEvent
import com.bearminds.architecture.BaseViewModel.ViewState
import com.bearminds.bearguard.traffic.model.Connection

/**
 * MVI Contract for the Traffic screen.
 */
object TrafficContract {

    data class State(
        val isLoading: Boolean = true,
        val connections: List<Connection> = emptyList(),
        val totalBytesIn: Long = 0,
        val totalBytesOut: Long = 0,
    ) : ViewState

    sealed interface Event : ViewEvent {
        data object LoadConnections : Event
        data object ClearConnections : Event
    }
}
