package com.bearminds.bearguard.rules

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.bearminds.bearguard.rules.data.RulesRepositoryImpl
import com.bearminds.bearguard.rules.model.Rule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okio.Path.Companion.toPath
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalUuidApi::class)
class RulesRepositoryImplTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var testFilePath: String

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        // Create unique temp file for each test
        testFilePath = "/tmp/test_prefs_${Uuid.random()}.preferences_pb"
        dataStore = PreferenceDataStoreFactory.createWithPath(
            produceFile = { testFilePath.toPath() }
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        // Clean up temp file
        try {
            java.io.File(testFilePath).delete()
        } catch (_: Exception) {
            // Ignore cleanup errors
        }
    }

    private fun createRepository() = RulesRepositoryImpl(dataStore = dataStore)

    // ===================
    // GetRules Tests
    // ===================

    @Test
    fun `getRules returns empty list when no rules stored`() = runTest {
        val repository = createRepository()

        val rules = repository.getRules()

        assertTrue(rules.isEmpty())
    }

    @Test
    fun `getRules returns stored rules after saveRule`() = runTest {
        val repository = createRepository()

        repository.saveRule(Rule(packageName = "com.app1", isAllowed = true))
        repository.saveRule(Rule(packageName = "com.app2", isAllowed = false))

        val rules = repository.getRules()

        assertEquals(2, rules.size)
    }

    // ===================
    // SaveRule Tests
    // ===================

    @Test
    fun `saveRule adds new rule`() = runTest {
        val repository = createRepository()

        repository.saveRule(Rule(packageName = "com.app1", isAllowed = false))

        val rule = repository.getRule("com.app1")
        assertEquals("com.app1", rule?.packageName)
        assertFalse(rule?.isAllowed ?: true)
    }

    @Test
    fun `saveRule updates existing rule`() = runTest {
        val repository = createRepository()

        repository.saveRule(Rule(packageName = "com.app1", isAllowed = true))
        repository.saveRule(Rule(packageName = "com.app1", isAllowed = false))

        val rules = repository.getRules()
        assertEquals(1, rules.size) // Only one rule for the package
        assertFalse(rules[0].isAllowed)
    }

    // ===================
    // GetRule Tests
    // ===================

    @Test
    fun `getRule returns null when no rule for package`() = runTest {
        val repository = createRepository()

        val rule = repository.getRule("com.nonexistent")

        assertNull(rule)
    }

    @Test
    fun `getRule returns rule when exists`() = runTest {
        val repository = createRepository()
        repository.saveRule(Rule(packageName = "com.app1", isAllowed = false))

        val rule = repository.getRule("com.app1")

        assertEquals("com.app1", rule?.packageName)
        assertFalse(rule?.isAllowed ?: true)
    }

    // ===================
    // DeleteRule Tests
    // ===================

    @Test
    fun `deleteRule removes rule`() = runTest {
        val repository = createRepository()
        repository.saveRule(Rule(packageName = "com.app1", isAllowed = false))

        repository.deleteRule("com.app1")

        val rule = repository.getRule("com.app1")
        assertNull(rule)
    }

    @Test
    fun `deleteRule does nothing when rule does not exist`() = runTest {
        val repository = createRepository()

        repository.deleteRule("com.nonexistent") // Should not throw

        assertTrue(repository.getRules().isEmpty())
    }

    // ===================
    // IsPackageAllowed Tests
    // ===================

    @Test
    fun `isPackageAllowed returns true when no rule exists`() = runTest {
        val repository = createRepository()

        val isAllowed = repository.isPackageAllowed("com.unknown")

        assertTrue(isAllowed)
    }

    @Test
    fun `isPackageAllowed returns true when rule allows`() = runTest {
        val repository = createRepository()
        repository.saveRule(Rule(packageName = "com.app1", isAllowed = true))

        val isAllowed = repository.isPackageAllowed("com.app1")

        assertTrue(isAllowed)
    }

    @Test
    fun `isPackageAllowed returns false when rule blocks`() = runTest {
        val repository = createRepository()
        repository.saveRule(Rule(packageName = "com.app1", isAllowed = false))

        val isAllowed = repository.isPackageAllowed("com.app1")

        assertFalse(isAllowed)
    }

    // ===================
    // GetBlockedPackages Tests
    // ===================

    @Test
    fun `getBlockedPackages returns empty set when no rules`() = runTest {
        val repository = createRepository()

        val blocked = repository.getBlockedPackages()

        assertTrue(blocked.isEmpty())
    }

    @Test
    fun `getBlockedPackages returns only blocked packages`() = runTest {
        val repository = createRepository()
        repository.saveRule(Rule(packageName = "com.allowed1", isAllowed = true))
        repository.saveRule(Rule(packageName = "com.blocked1", isAllowed = false))
        repository.saveRule(Rule(packageName = "com.allowed2", isAllowed = true))
        repository.saveRule(Rule(packageName = "com.blocked2", isAllowed = false))

        val blocked = repository.getBlockedPackages()

        assertEquals(2, blocked.size)
        assertTrue(blocked.contains("com.blocked1"))
        assertTrue(blocked.contains("com.blocked2"))
        assertFalse(blocked.contains("com.allowed1"))
    }

    // ===================
    // ObserveRules Tests
    // ===================

    @Test
    fun `observeRules emits empty list initially when no rules`() = runTest {
        val repository = createRepository()

        val rules = repository.observeRules().first()

        assertTrue(rules.isEmpty())
    }

    @Test
    fun `observeRules emits rules when stored`() = runTest {
        val repository = createRepository()
        repository.saveRule(Rule(packageName = "com.app1", isAllowed = true))

        val rules = repository.observeRules().first()

        assertEquals(1, rules.size)
    }

    // ===================
    // Rule Properties Tests
    // ===================

    @Test
    fun `rules with wifi and mobile data settings are preserved`() = runTest {
        val repository = createRepository()
        repository.saveRule(
            Rule(
                packageName = "com.app1",
                isAllowed = true,
                allowWifi = false,
                allowMobileData = true,
            )
        )

        val rule = repository.getRule("com.app1")

        assertEquals("com.app1", rule?.packageName)
        assertTrue(rule?.isAllowed == true)
        assertFalse(rule?.allowWifi == true)
        assertTrue(rule?.allowMobileData == true)
    }
}
