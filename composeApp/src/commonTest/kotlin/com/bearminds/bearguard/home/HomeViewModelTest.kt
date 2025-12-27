package com.bearminds.bearguard.home

import bearguard.composeapp.generated.resources.Res
import bearguard.composeapp.generated.resources.vpn_connection_failed
import com.bearminds.architecture.BaseViewModel
import com.bearminds.bearguard.vpn.VpnController
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import dev.mokkery.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var vpnController: VpnController
    private val isRunningFlow = MutableStateFlow(false)

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        vpnController = mock()
        every { vpnController.isRunning } returns isRunningFlow
        every { vpnController.requiresPermission } returns true
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = HomeViewModel(
        vpnController = vpnController,
    )

    // ===================
    // Initial State Tests
    // ===================

    @Test
    fun `initial state has isVpnRunning false and isLoading false`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.viewState.value
        assertFalse(state.isVpnRunning)
        assertFalse(state.isLoading)
    }

    // ===================
    // VPN State Observation Tests
    // ===================

    @Test
    fun `when vpn controller isRunning changes to true then state updates`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        // Initially false
        assertFalse(viewModel.viewState.value.isVpnRunning)

        // Change VPN state
        isRunningFlow.value = true
        advanceUntilIdle()

        assertTrue(viewModel.viewState.value.isVpnRunning)
    }

    @Test
    fun `when vpn controller isRunning changes to false then state updates`() = runTest {
        isRunningFlow.value = true
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.viewState.value.isVpnRunning)

        // Change VPN state back to false
        isRunningFlow.value = false
        advanceUntilIdle()

        assertFalse(viewModel.viewState.value.isVpnRunning)
    }

    // ===================
    // Toggle VPN Event Tests
    // ===================

    @Test
    fun `when ToggleVpn event and vpn is off then calls vpnController toggle`() = runTest {
        every { vpnController.toggle() } returns Unit
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(HomeContract.Event.ToggleVpn)
        advanceUntilIdle()

        verify { vpnController.toggle() }
    }

    @Test
    fun `when ToggleVpn event then isLoading is set before vpn state changes`() = runTest {
        every { vpnController.toggle() } returns Unit
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertFalse(viewModel.viewState.value.isLoading)

        viewModel.onEvent(HomeContract.Event.ToggleVpn)
        // Advance only a small amount - not past the 3-second timeout
        advanceTimeBy(100)

        // After toggle but before VPN state change, loading should be true
        // (VPN state change hasn't happened yet since we control isRunningFlow)
        assertTrue(viewModel.viewState.value.isLoading)

        // Once VPN state changes, loading becomes false
        isRunningFlow.value = true
        advanceUntilIdle()
        assertFalse(viewModel.viewState.value.isLoading)
    }

    @Test
    fun `when ToggleVpn event and vpn state does not change then loading resets after timeout`() = runTest {
        every { vpnController.toggle() } returns Unit
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(HomeContract.Event.ToggleVpn)
        advanceTimeBy(100)

        // Loading should be true initially
        assertTrue(viewModel.viewState.value.isLoading)

        // Advance past the 3-second timeout
        advanceTimeBy(3000)

        // Loading should reset to false after timeout
        assertFalse(viewModel.viewState.value.isLoading)
    }

    @Test
    fun `when connection times out then snackbar effect is emitted`() = runTest {
        every { vpnController.toggle() } returns Unit
        val viewModel = createViewModel()
        advanceUntilIdle()

        // Collect the effect in a separate coroutine
        var receivedEffect: BaseViewModel.ViewEffect? = null
        val effectJob = launch {
            receivedEffect = viewModel.effect.first()
        }

        viewModel.onEvent(HomeContract.Event.ToggleVpn)
        // Advance past the 3-second timeout
        advanceTimeBy(3100)

        // Verify snackbar effect was emitted
        assertIs<BaseViewModel.SnackbarResourceEffect>(receivedEffect)
        val snackbarEffect = receivedEffect as BaseViewModel.SnackbarResourceEffect
        assertEquals(Res.string.vpn_connection_failed, snackbarEffect.data.messageRes)

        effectJob.cancel()
    }

    @Test
    fun `when vpn state changes before timeout then timeout is cancelled`() = runTest {
        every { vpnController.toggle() } returns Unit
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(HomeContract.Event.ToggleVpn)
        advanceTimeBy(100)
        assertTrue(viewModel.viewState.value.isLoading)

        // VPN state changes before timeout
        isRunningFlow.value = true
        advanceTimeBy(100)

        // Loading should be false due to VPN state change, not timeout
        assertFalse(viewModel.viewState.value.isLoading)
        assertTrue(viewModel.viewState.value.isVpnRunning)
    }

    @Test
    fun `when vpn state changes after toggle then isLoading becomes false`() = runTest {
        every { vpnController.toggle() } returns Unit
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(HomeContract.Event.ToggleVpn)
        advanceUntilIdle()

        // Simulate VPN state change (which should reset loading)
        isRunningFlow.value = true
        advanceUntilIdle()

        assertFalse(viewModel.viewState.value.isLoading)
        assertTrue(viewModel.viewState.value.isVpnRunning)
    }

    // ===================
    // Multiple State Changes Tests
    // ===================

    @Test
    fun `rapid vpn state changes are handled correctly`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        // Rapid state changes
        isRunningFlow.value = true
        advanceUntilIdle()
        assertTrue(viewModel.viewState.value.isVpnRunning)

        isRunningFlow.value = false
        advanceUntilIdle()
        assertFalse(viewModel.viewState.value.isVpnRunning)

        isRunningFlow.value = true
        advanceUntilIdle()
        assertTrue(viewModel.viewState.value.isVpnRunning)
    }
}
