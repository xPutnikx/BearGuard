package com.bearminds.bearguard.home

import androidx.lifecycle.viewModelScope
import com.bearminds.architecture.BaseViewModel
import com.bearminds.architecture.StyledSnackbarResourceData
import com.bearminds.bearguard.vpn.VpnController
import bearguard.composeapp.generated.resources.Res
import bearguard.composeapp.generated.resources.vpn_connection_failed
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class HomeViewModel(
    private val vpnController: VpnController,
) : BaseViewModel<HomeContract.Event, HomeContract.State>() {

    override val initialState = HomeContract.State()

    private var loadingTimeoutJob: Job? = null

    init {
        observeVpnState()
    }

    private fun observeVpnState() {
        vpnController.isRunning
            .onEach { isRunning ->
                loadingTimeoutJob?.cancel()
                setState { copy(isVpnRunning = isRunning, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    override fun handleEvent(event: HomeContract.Event) {
        when (event) {
            is HomeContract.Event.ToggleVpn -> toggleVpn()
            is HomeContract.Event.VpnStateChanged -> { /* State already observed */ }
        }
    }

    private fun toggleVpn() {
        setState { copy(isLoading = true) }
        vpnController.toggle()

        // Timeout to reset loading if VPN state doesn't change
        loadingTimeoutJob?.cancel()
        loadingTimeoutJob = viewModelScope.launch {
            delay(3000) // 3 second timeout
            setState { copy(isLoading = false) }
            setEffect {
                SnackbarResourceEffect(
                    StyledSnackbarResourceData(messageRes = Res.string.vpn_connection_failed)
                )
            }
        }
    }
}
