package com.bearminds.bearguard.rules.data

import com.bearminds.bearguard.rules.model.Rule
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing firewall rules.
 */
interface RulesRepository {

    /**
     * Observe all firewall rules.
     */
    fun observeRules(): Flow<List<Rule>>

    /**
     * Get the current list of all rules.
     */
    suspend fun getRules(): List<Rule>

    /**
     * Get the rule for a specific package, or null if no rule exists.
     */
    suspend fun getRule(packageName: String): Rule?

    /**
     * Save or update a rule.
     */
    suspend fun saveRule(rule: Rule)

    /**
     * Delete a rule for a specific package.
     */
    suspend fun deleteRule(packageName: String)

    /**
     * Check if a package is allowed network access.
     * Returns true if allowed (or no rule exists), false if blocked.
     */
    suspend fun isPackageAllowed(packageName: String): Boolean

    /**
     * Get all blocked package names.
     */
    suspend fun getBlockedPackages(): Set<String>
}
