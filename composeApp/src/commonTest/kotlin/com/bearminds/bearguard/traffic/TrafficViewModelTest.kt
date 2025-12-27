package com.bearminds.bearguard.traffic

import com.bearminds.bearguard.traffic.data.TrafficRepository
import com.bearminds.bearguard.traffic.model.Connection
import com.bearminds.bearguard.traffic.model.Protocol
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.every
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
class TrafficViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var trafficRepository: TrafficRepository
    private val connectionsFlow = MutableStateFlow<List<Connection>>(emptyList())

    private fun createTestConnection(
        id: Long = 1,
        packageName: String? = "com.example.app",
        uid: Int = 1001,
        destinationIp: String = "8.8.8.8",
        destinationPort: Int = 443,
        wasBlocked: Boolean = false,
        bytesIn: Long = 1024,
        bytesOut: Long = 512,
    ) = Connection(
        id = id,
        packageName = packageName,
        uid = uid,
        protocol = Protocol.TCP,
        sourceIp = "10.0.0.2",
        sourcePort = 54321,
        destinationIp = destinationIp,
        destinationPort = destinationPort,
        timestamp = System.currentTimeMillis(),
        bytesIn = bytesIn,
        bytesOut = bytesOut,
        wasBlocked = wasBlocked,
    )

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        trafficRepository = mock()
        every { trafficRepository.observeConnections() } returns connectionsFlow
        everySuspend { trafficRepository.getTotalBytes() } returns (0L to 0L)
        everySuspend { trafficRepository.clearConnections() } returns Unit
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = TrafficViewModel(
        trafficRepository = trafficRepository,
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

    @Test
    fun `initial state has empty connections`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.viewState.value.connections.isEmpty())
    }

    // ===================
    // Connections Observation Tests
    // ===================

    @Test
    fun `when connections flow emits then state updates`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.viewState.value.connections.isEmpty())

        val testConnections = listOf(
            createTestConnection(id = 1),
            createTestConnection(id = 2),
        )
        connectionsFlow.value = testConnections
        advanceUntilIdle()

        assertEquals(2, viewModel.viewState.value.connections.size)
        assertFalse(viewModel.viewState.value.isLoading)
    }

    @Test
    fun `when connections change then isLoading becomes false`() = runTest {
        val viewModel = createViewModel()

        // After first emission, loading should be false
        connectionsFlow.value = emptyList()
        advanceUntilIdle()

        assertFalse(viewModel.viewState.value.isLoading)
    }

    // ===================
    // Bytes Tracking Tests
    // ===================

    @Test
    fun `when connections update then total bytes are calculated`() = runTest {
        everySuspend { trafficRepository.getTotalBytes() } returns (2048L to 1024L)

        val viewModel = createViewModel()
        connectionsFlow.value = listOf(createTestConnection())
        advanceUntilIdle()

        assertEquals(2048L, viewModel.viewState.value.totalBytesIn)
        assertEquals(1024L, viewModel.viewState.value.totalBytesOut)
    }

    @Test
    fun `when no connections then bytes are zero`() = runTest {
        everySuspend { trafficRepository.getTotalBytes() } returns (0L to 0L)

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(0L, viewModel.viewState.value.totalBytesIn)
        assertEquals(0L, viewModel.viewState.value.totalBytesOut)
    }

    // ===================
    // ClearConnections Event Tests
    // ===================

    @Test
    fun `when ClearConnections then calls repository clearConnections`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(TrafficContract.Event.ClearConnections)
        advanceUntilIdle()

        verifySuspend { trafficRepository.clearConnections() }
    }

    // ===================
    // Connection Types Tests
    // ===================

    @Test
    fun `blocked connections are included in list`() = runTest {
        val viewModel = createViewModel()

        val blockedConnection = createTestConnection(id = 1, wasBlocked = true)
        connectionsFlow.value = listOf(blockedConnection)
        advanceUntilIdle()

        assertEquals(1, viewModel.viewState.value.connections.size)
        assertTrue(viewModel.viewState.value.connections[0].wasBlocked)
    }

    @Test
    fun `connections with unknown package are included`() = runTest {
        val viewModel = createViewModel()

        val unknownConnection = createTestConnection(id = 1, packageName = null)
        connectionsFlow.value = listOf(unknownConnection)
        advanceUntilIdle()

        assertEquals(1, viewModel.viewState.value.connections.size)
        assertEquals(null, viewModel.viewState.value.connections[0].packageName)
    }

    // ===================
    // Multiple Updates Tests
    // ===================

    @Test
    fun `multiple connection updates are handled correctly`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        // First update
        connectionsFlow.value = listOf(createTestConnection(id = 1))
        advanceUntilIdle()
        assertEquals(1, viewModel.viewState.value.connections.size)

        // Second update - more connections
        connectionsFlow.value = listOf(
            createTestConnection(id = 1),
            createTestConnection(id = 2),
            createTestConnection(id = 3),
        )
        advanceUntilIdle()
        assertEquals(3, viewModel.viewState.value.connections.size)

        // Third update - fewer connections (after clear)
        connectionsFlow.value = emptyList()
        advanceUntilIdle()
        assertEquals(0, viewModel.viewState.value.connections.size)
    }
}
