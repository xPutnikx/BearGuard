package com.bearminds.bearguard.rules.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.bearminds.bearguard.rules.model.Rule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * DataStore-based implementation of [RulesRepository].
 *
 * Stores rules as a JSON-encoded map in DataStore preferences.
 */
class RulesRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
) : RulesRepository {

    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        private val RULES_KEY = stringPreferencesKey("firewall_rules")
    }

    override fun observeRules(): Flow<List<Rule>> {
        return dataStore.data.map { preferences ->
            preferences[RULES_KEY]?.let { rulesJson ->
                decodeRules(rulesJson)
            } ?: emptyList()
        }
    }

    override suspend fun getRules(): List<Rule> {
        val preferences = dataStore.data.first()
        return preferences[RULES_KEY]?.let { rulesJson ->
            decodeRules(rulesJson)
        } ?: emptyList()
    }

    override suspend fun getRule(packageName: String): Rule? {
        return getRules().find { it.packageName == packageName }
    }

    override suspend fun saveRule(rule: Rule) {
        dataStore.edit { preferences ->
            val currentRules = preferences[RULES_KEY]?.let { decodeRules(it) } ?: emptyList()
            val updatedRules = currentRules.filter { it.packageName != rule.packageName } + rule
            preferences[RULES_KEY] = encodeRules(updatedRules)
        }
    }

    override suspend fun deleteRule(packageName: String) {
        dataStore.edit { preferences ->
            val currentRules = preferences[RULES_KEY]?.let { decodeRules(it) } ?: emptyList()
            val updatedRules = currentRules.filter { it.packageName != packageName }
            preferences[RULES_KEY] = encodeRules(updatedRules)
        }
    }

    override suspend fun isPackageAllowed(packageName: String): Boolean {
        val rule = getRule(packageName)
        return rule?.isAllowed ?: true // Default: allow if no rule
    }

    override suspend fun getBlockedPackages(): Set<String> {
        return getRules()
            .filter { !it.isAllowed }
            .map { it.packageName }
            .toSet()
    }

    private fun encodeRules(rules: List<Rule>): String {
        return json.encodeToString(rules)
    }

    private fun decodeRules(rulesJson: String): List<Rule> {
        return try {
            json.decodeFromString(rulesJson)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
