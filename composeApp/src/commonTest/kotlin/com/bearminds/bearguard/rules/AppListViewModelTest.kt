package com.bearminds.bearguard.rules

import com.bearminds.bearguard.rules.data.AppListProvider
import com.bearminds.bearguard.rules.data.RulesRepository
import com.bearminds.bearguard.rules.model.AppInfo
import com.bearminds.bearguard.rules.model.Rule
import com.bearminds.bearguard.rules.ui.AppListContract
import com.bearminds.bearguard.rules.ui.AppListViewModel
import com.bearminds.bearguard.settings.data.SettingsRepository
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class AppListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var appListProvider: AppListProvider
    private lateinit var rulesRepository: RulesRepository
    private lateinit var settingsRepository: SettingsRepository

    private val testApps = listOf(
        AppInfo(packageName = "com.example.app1", name = "App One", isSystemApp = false, uid = 1001),
        AppInfo(packageName = "com.example.app2", name = "App Two", isSystemApp = false, uid = 1002),
        AppInfo(packageName = "com.android.system", name = "System App", isSystemApp = true, uid = 1003),
    )

    private val testRules = listOf(
        Rule(packageName = "com.example.app2", isAllowed = false),
    )

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        appListProvider = mock()
        rulesRepository = mock()
        settingsRepository = mock()

        everySuspend { appListProvider.getInstalledApps(false) } returns testApps.filter { !it.isSystemApp }
        everySuspend { appListProvider.getInstalledApps(true) } returns testApps
        everySuspend { rulesRepository.getRules() } returns testRules
        everySuspend { rulesRepository.saveRule(any()) } returns Unit
        everySuspend { settingsRepository.getShowSystemAppsByDefault() } returns false
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = AppListViewModel(
        appListProvider = appListProvider,
        rulesRepository = rulesRepository,
        settingsRepository = settingsRepository,
    )

    // ===================
    // Initial State Tests
    // ===================

    @Test
    fun `initial state has isLoading true`() = runTest {
        val viewModel = createViewModel()
        // Don't advance - check initial state
        assertTrue(viewModel.viewState.value.isLoading)
    }

    // ===================
    // LoadApps Event Tests
    // ===================

    @Test
    fun `when LoadApps then loads apps from provider`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.viewState.value
        assertFalse(state.isLoading)
        assertEquals(2, state.apps.size) // Only non-system apps by default
    }

    @Test
    fun `when LoadApps then applies existing rules`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.viewState.value
        val app1 = state.apps.find { it.app.packageName == "com.example.app1" }
        val app2 = state.apps.find { it.app.packageName == "com.example.app2" }

        assertTrue(app1?.isAllowed == true) // No rule = allowed
        assertTrue(app2?.isAllowed == false) // Has blocking rule
    }

    @Test
    fun `when LoadApps with no rules then all apps are allowed`() = runTest {
        everySuspend { rulesRepository.getRules() } returns emptyList()

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.viewState.value
        assertTrue(state.apps.all { it.isAllowed })
    }

    // ===================
    // ToggleSystemApps Event Tests
    // ===================

    @Test
    fun `when ToggleSystemApps to true then shows system apps`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(2, viewModel.viewState.value.apps.size)

        viewModel.onEvent(AppListContract.Event.ToggleSystemApps(true))
        advanceUntilIdle()

        assertTrue(viewModel.viewState.value.showSystemApps)
        assertEquals(3, viewModel.viewState.value.apps.size)
    }

    @Test
    fun `when ToggleSystemApps to false then hides system apps`() = runTest {
        everySuspend { appListProvider.getInstalledApps(true) } returns testApps

        val viewModel = createViewModel()
        viewModel.onEvent(AppListContract.Event.ToggleSystemApps(true))
        advanceUntilIdle()

        assertEquals(3, viewModel.viewState.value.apps.size)

        viewModel.onEvent(AppListContract.Event.ToggleSystemApps(false))
        advanceUntilIdle()

        assertFalse(viewModel.viewState.value.showSystemApps)
        assertEquals(2, viewModel.viewState.value.apps.size)
    }

    // ===================
    // ToggleAppAccess Event Tests
    // ===================

    @Test
    fun `when ToggleAppAccess to block then saves rule and updates state`() = runTest {
        everySuspend { rulesRepository.saveRule(any()) } returns Unit

        val viewModel = createViewModel()
        advanceUntilIdle()

        val app1Before = viewModel.viewState.value.apps.find { it.app.packageName == "com.example.app1" }
        assertTrue(app1Before?.isAllowed == true)

        viewModel.onEvent(AppListContract.Event.ToggleAppAccess("com.example.app1", false))
        advanceUntilIdle()

        val app1After = viewModel.viewState.value.apps.find { it.app.packageName == "com.example.app1" }
        assertFalse(app1After?.isAllowed == true)
    }

    @Test
    fun `when ToggleAppAccess to allow then saves rule and updates state`() = runTest {
        everySuspend { rulesRepository.saveRule(any()) } returns Unit

        val viewModel = createViewModel()
        advanceUntilIdle()

        val app2Before = viewModel.viewState.value.apps.find { it.app.packageName == "com.example.app2" }
        assertFalse(app2Before?.isAllowed == true)

        viewModel.onEvent(AppListContract.Event.ToggleAppAccess("com.example.app2", true))
        advanceUntilIdle()

        val app2After = viewModel.viewState.value.apps.find { it.app.packageName == "com.example.app2" }
        assertTrue(app2After?.isAllowed == true)
    }

    // ===================
    // UpdateSearchQuery Event Tests
    // ===================

    @Test
    fun `when UpdateSearchQuery then updates searchQuery in state`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals("", viewModel.viewState.value.searchQuery)

        viewModel.onEvent(AppListContract.Event.UpdateSearchQuery("test"))
        advanceUntilIdle()

        assertEquals("test", viewModel.viewState.value.searchQuery)
    }

    @Test
    fun `search query filtering is done in screen not viewmodel`() = runTest {
        // Note: The ViewModel stores the query, but filtering happens in the Composable
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(AppListContract.Event.UpdateSearchQuery("App One"))
        advanceUntilIdle()

        // ViewModel still has all apps, filtering is in UI layer
        assertEquals(2, viewModel.viewState.value.apps.size)
        assertEquals("App One", viewModel.viewState.value.searchQuery)
    }

    // ===================
    // Empty State Tests
    // ===================

    @Test
    fun `when no apps installed then apps list is empty`() = runTest {
        everySuspend { appListProvider.getInstalledApps(false) } returns emptyList()

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.viewState.value.apps.isEmpty())
        assertFalse(viewModel.viewState.value.isLoading)
    }

    // ===================
    // Settings Integration Tests
    // ===================

    @Test
    fun `when showSystemAppsByDefault is true then initially shows system apps`() = runTest {
        everySuspend { settingsRepository.getShowSystemAppsByDefault() } returns true

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.viewState.value.showSystemApps)
        assertEquals(3, viewModel.viewState.value.apps.size)
    }

    @Test
    fun `when showSystemAppsByDefault is false then initially hides system apps`() = runTest {
        everySuspend { settingsRepository.getShowSystemAppsByDefault() } returns false

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertFalse(viewModel.viewState.value.showSystemApps)
        assertEquals(2, viewModel.viewState.value.apps.size)
    }
}
