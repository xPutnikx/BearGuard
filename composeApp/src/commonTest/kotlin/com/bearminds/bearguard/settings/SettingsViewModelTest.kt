package com.bearminds.bearguard.settings

import com.bearminds.bearguard.settings.SettingsContract.DefaultRule
import com.bearminds.bearguard.settings.SettingsContract.ThemeMode
import com.bearminds.bearguard.settings.data.SettingsRepository
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var settingsRepository: SettingsRepository
    private val themeModeFlow = MutableStateFlow(ThemeMode.SYSTEM)
    private val defaultRuleFlow = MutableStateFlow(DefaultRule.ALLOW)
    private val showSystemAppsFlow = MutableStateFlow(false)

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        settingsRepository = mock()
        every { settingsRepository.observeThemeMode() } returns themeModeFlow
        every { settingsRepository.observeDefaultRuleForNewApps() } returns defaultRuleFlow
        every { settingsRepository.observeShowSystemAppsByDefault() } returns showSystemAppsFlow
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = SettingsViewModel(
        settingsRepository = settingsRepository,
    )

    // ===================
    // Initial State Tests
    // ===================

    @Test
    fun `initial state has default values`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.viewState.value
        assertEquals(ThemeMode.SYSTEM, state.themeMode)
        assertEquals(DefaultRule.ALLOW, state.defaultRuleForNewApps)
        assertFalse(state.showSystemAppsByDefault)
    }

    @Test
    fun `initial state reflects repository values`() = runTest {
        themeModeFlow.value = ThemeMode.DARK
        defaultRuleFlow.value = DefaultRule.BLOCK
        showSystemAppsFlow.value = true

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.viewState.value
        assertEquals(ThemeMode.DARK, state.themeMode)
        assertEquals(DefaultRule.BLOCK, state.defaultRuleForNewApps)
        assertTrue(state.showSystemAppsByDefault)
    }

    // ===================
    // Theme Mode Tests
    // ===================

    @Test
    fun `when theme mode changes in repository then state updates`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(ThemeMode.SYSTEM, viewModel.viewState.value.themeMode)

        themeModeFlow.value = ThemeMode.DARK
        advanceUntilIdle()

        assertEquals(ThemeMode.DARK, viewModel.viewState.value.themeMode)
    }

    @Test
    fun `when SetThemeMode event then repository is called`() = runTest {
        everySuspend { settingsRepository.setThemeMode(ThemeMode.LIGHT) } returns Unit
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(SettingsContract.Event.SetThemeMode(ThemeMode.LIGHT))
        advanceUntilIdle()

        verifySuspend { settingsRepository.setThemeMode(ThemeMode.LIGHT) }
    }

    @Test
    fun `when SetThemeMode to DARK then repository is called with DARK`() = runTest {
        everySuspend { settingsRepository.setThemeMode(ThemeMode.DARK) } returns Unit
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(SettingsContract.Event.SetThemeMode(ThemeMode.DARK))
        advanceUntilIdle()

        verifySuspend { settingsRepository.setThemeMode(ThemeMode.DARK) }
    }

    @Test
    fun `when SetThemeMode to SYSTEM then repository is called with SYSTEM`() = runTest {
        themeModeFlow.value = ThemeMode.DARK
        everySuspend { settingsRepository.setThemeMode(ThemeMode.SYSTEM) } returns Unit
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(SettingsContract.Event.SetThemeMode(ThemeMode.SYSTEM))
        advanceUntilIdle()

        verifySuspend { settingsRepository.setThemeMode(ThemeMode.SYSTEM) }
    }

    // ===================
    // Default Rule Tests
    // ===================

    @Test
    fun `when default rule changes in repository then state updates`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(DefaultRule.ALLOW, viewModel.viewState.value.defaultRuleForNewApps)

        defaultRuleFlow.value = DefaultRule.BLOCK
        advanceUntilIdle()

        assertEquals(DefaultRule.BLOCK, viewModel.viewState.value.defaultRuleForNewApps)
    }

    @Test
    fun `when SetDefaultRuleForNewApps event to ALLOW then repository is called`() = runTest {
        defaultRuleFlow.value = DefaultRule.BLOCK
        everySuspend { settingsRepository.setDefaultRuleForNewApps(DefaultRule.ALLOW) } returns Unit
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(SettingsContract.Event.SetDefaultRuleForNewApps(DefaultRule.ALLOW))
        advanceUntilIdle()

        verifySuspend { settingsRepository.setDefaultRuleForNewApps(DefaultRule.ALLOW) }
    }

    @Test
    fun `when SetDefaultRuleForNewApps event to BLOCK then repository is called`() = runTest {
        everySuspend { settingsRepository.setDefaultRuleForNewApps(DefaultRule.BLOCK) } returns Unit
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(SettingsContract.Event.SetDefaultRuleForNewApps(DefaultRule.BLOCK))
        advanceUntilIdle()

        verifySuspend { settingsRepository.setDefaultRuleForNewApps(DefaultRule.BLOCK) }
    }

    // ===================
    // Show System Apps Tests
    // ===================

    @Test
    fun `when show system apps changes in repository then state updates`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertFalse(viewModel.viewState.value.showSystemAppsByDefault)

        showSystemAppsFlow.value = true
        advanceUntilIdle()

        assertTrue(viewModel.viewState.value.showSystemAppsByDefault)
    }

    @Test
    fun `when SetShowSystemAppsByDefault event to true then repository is called`() = runTest {
        everySuspend { settingsRepository.setShowSystemAppsByDefault(true) } returns Unit
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(SettingsContract.Event.SetShowSystemAppsByDefault(true))
        advanceUntilIdle()

        verifySuspend { settingsRepository.setShowSystemAppsByDefault(true) }
    }

    @Test
    fun `when SetShowSystemAppsByDefault event to false then repository is called`() = runTest {
        showSystemAppsFlow.value = true
        everySuspend { settingsRepository.setShowSystemAppsByDefault(false) } returns Unit
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(SettingsContract.Event.SetShowSystemAppsByDefault(false))
        advanceUntilIdle()

        verifySuspend { settingsRepository.setShowSystemAppsByDefault(false) }
    }

    // ===================
    // Combined State Tests
    // ===================

    @Test
    fun `state reflects all combined repository values`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        // Change all values
        themeModeFlow.value = ThemeMode.LIGHT
        defaultRuleFlow.value = DefaultRule.BLOCK
        showSystemAppsFlow.value = true
        advanceUntilIdle()

        val state = viewModel.viewState.value
        assertEquals(ThemeMode.LIGHT, state.themeMode)
        assertEquals(DefaultRule.BLOCK, state.defaultRuleForNewApps)
        assertTrue(state.showSystemAppsByDefault)
    }

    @Test
    fun `partial state updates work correctly`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        // Only change theme mode
        themeModeFlow.value = ThemeMode.DARK
        advanceUntilIdle()

        var state = viewModel.viewState.value
        assertEquals(ThemeMode.DARK, state.themeMode)
        assertEquals(DefaultRule.ALLOW, state.defaultRuleForNewApps)
        assertFalse(state.showSystemAppsByDefault)

        // Then change show system apps
        showSystemAppsFlow.value = true
        advanceUntilIdle()

        state = viewModel.viewState.value
        assertEquals(ThemeMode.DARK, state.themeMode)
        assertEquals(DefaultRule.ALLOW, state.defaultRuleForNewApps)
        assertTrue(state.showSystemAppsByDefault)
    }
}
