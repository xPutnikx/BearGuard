package com.bearminds.bearguard.traffic.data

import com.bearminds.bearguard.traffic.model.Connection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

/**
 * In-memory implementation of [TrafficRepository].
 * For production, this should be backed by a database.
 */
class TrafficRepositoryImpl : TrafficRepository {

    private val _connections = MutableStateFlow<List<Connection>>(emptyList())
    private var connectionIdCounter = 0L

    override fun observeConnections(): Flow<List<Connection>> {
        return _connections.asStateFlow().map { connections ->
            connections.sortedByDescending { it.timestamp }
        }
    }

    override suspend fun logConnection(connection: Connection) {
        val newConnection = connection.copy(id = ++connectionIdCounter)
        _connections.value = _connections.value + newConnection

        // Keep only last 1000 connections to prevent memory issues
        if (_connections.value.size > 1000) {
            _connections.value = _connections.value.takeLast(1000)
        }
    }

    override suspend fun getConnectionsForPackage(packageName: String): List<Connection> {
        return _connections.value.filter { it.packageName == packageName }
    }

    override suspend fun clearConnections() {
        _connections.value = emptyList()
    }

    override suspend fun getTotalBytes(): Pair<Long, Long> {
        val bytesIn = _connections.value.sumOf { it.bytesIn }
        val bytesOut = _connections.value.sumOf { it.bytesOut }
        return bytesIn to bytesOut
    }
}
