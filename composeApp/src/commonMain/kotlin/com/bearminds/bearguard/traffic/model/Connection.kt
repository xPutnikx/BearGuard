package com.bearminds.bearguard.traffic.model

import kotlinx.serialization.Serializable

/**
 * Represents a network connection/traffic log entry.
 *
 * @param id Unique identifier for this connection
 * @param packageName The app's package identifier that initiated the connection
 * @param uid The app's UID
 * @param protocol Network protocol (TCP, UDP, ICMP)
 * @param sourceIp Source IP address
 * @param sourcePort Source port number
 * @param destinationIp Destination IP address
 * @param destinationPort Destination port number
 * @param timestamp When the connection was detected (epoch millis)
 * @param bytesIn Bytes received
 * @param bytesOut Bytes sent
 * @param wasBlocked Whether this connection was blocked by the firewall
 */
@Serializable
data class Connection(
    val id: Long = 0,
    val packageName: String?,
    val uid: Int,
    val protocol: Protocol,
    val sourceIp: String,
    val sourcePort: Int,
    val destinationIp: String,
    val destinationPort: Int,
    val timestamp: Long,
    val bytesIn: Long = 0,
    val bytesOut: Long = 0,
    val wasBlocked: Boolean = false,
)

@Serializable
enum class Protocol {
    TCP,
    UDP,
    ICMP,
    OTHER,
}
