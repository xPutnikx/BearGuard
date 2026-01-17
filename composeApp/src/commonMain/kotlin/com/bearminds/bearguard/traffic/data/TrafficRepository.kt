package com.bearminds.bearguard.traffic.data

import com.bearminds.bearguard.traffic.model.Connection
import com.bearminds.bearguard.traffic.model.TrafficStats
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing traffic/connection logs.
 */
interface TrafficRepository {

    /**
     * Observe all logged connections.
     */
    fun observeConnections(): Flow<List<Connection>>

    /**
     * Log a new connection.
     */
    suspend fun logConnection(connection: Connection)

    /**
     * Get connections for a specific package.
     */
    suspend fun getConnectionsForPackage(packageName: String): List<Connection>

    /**
     * Clear all logged connections.
     */
    suspend fun clearConnections()

    /**
     * Get total bytes transferred.
     */
    suspend fun getTotalBytes(): Pair<Long, Long> // (bytesIn, bytesOut)

    /**
     * Get aggregated traffic stats for all apps.
     * Returns a map of package name to stats.
     */
    suspend fun getStatsPerApp(): Map<String, TrafficStats>

    /**
     * Observe aggregated traffic stats for all apps.
     */
    fun observeStatsPerApp(): Flow<Map<String, TrafficStats>>
}
