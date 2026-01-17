package com.bearminds.bearguard.traffic.model

/**
 * Aggregated traffic statistics for an app.
 *
 * @param packageName The app's package identifier
 * @param bytesIn Total bytes received
 * @param bytesOut Total bytes sent
 * @param connectionCount Number of connections made
 * @param lastConnectionTime Timestamp of most recent connection (epoch millis)
 */
data class TrafficStats(
    val packageName: String,
    val bytesIn: Long = 0,
    val bytesOut: Long = 0,
    val connectionCount: Int = 0,
    val lastConnectionTime: Long = 0,
) {
    /**
     * Total bytes transferred (in + out).
     */
    val totalBytes: Long get() = bytesIn + bytesOut
}
