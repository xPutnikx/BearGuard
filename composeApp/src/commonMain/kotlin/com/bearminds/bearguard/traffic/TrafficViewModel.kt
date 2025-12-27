package com.bearminds.bearguard.traffic

import androidx.lifecycle.viewModelScope
import com.bearminds.architecture.BaseViewModel
import com.bearminds.bearguard.traffic.data.TrafficRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class TrafficViewModel(
    private val trafficRepository: TrafficRepository,
) : BaseViewModel<TrafficContract.Event, TrafficContract.State>() {

    override val initialState = TrafficContract.State()

    init {
        observeConnections()
    }

    private fun observeConnections() {
        trafficRepository.observeConnections()
            .onEach { connections ->
                val (bytesIn, bytesOut) = trafficRepository.getTotalBytes()
                setState {
                    copy(
                        isLoading = false,
                        connections = connections,
                        totalBytesIn = bytesIn,
                        totalBytesOut = bytesOut,
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    override fun handleEvent(event: TrafficContract.Event) {
        when (event) {
            is TrafficContract.Event.LoadConnections -> { /* Already observing */ }
            is TrafficContract.Event.ClearConnections -> clearConnections()
        }
    }

    private fun clearConnections() {
        viewModelScope.launch {
            trafficRepository.clearConnections()
        }
    }
}
