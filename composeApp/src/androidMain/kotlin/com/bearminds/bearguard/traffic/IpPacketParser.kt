package com.bearminds.bearguard.traffic

import com.bearminds.bearguard.traffic.model.Protocol
import java.nio.ByteBuffer

/**
 * Parsed IP packet information.
 */
data class ParsedPacket(
    val protocol: Protocol,
    val sourceIp: String,
    val sourcePort: Int,
    val destinationIp: String,
    val destinationPort: Int,
    val packetLength: Int,
)

/**
 * Parser for IP packets (IPv4 and IPv6).
 * Extracts source/destination IPs, ports, and protocol information.
 */
object IpPacketParser {

    private const val IP_VERSION_4 = 4
    private const val IP_VERSION_6 = 6

    private const val PROTOCOL_ICMP = 1
    private const val PROTOCOL_TCP = 6
    private const val PROTOCOL_UDP = 17
    private const val PROTOCOL_ICMPV6 = 58

    /**
     * Parse an IP packet from a ByteBuffer.
     * Returns null if the packet cannot be parsed.
     */
    fun parse(buffer: ByteBuffer, length: Int): ParsedPacket? {
        if (length < 20) return null // Minimum IPv4 header size

        buffer.position(0)
        val versionAndIhl = buffer.get().toInt() and 0xFF
        val version = (versionAndIhl shr 4) and 0x0F

        return when (version) {
            IP_VERSION_4 -> parseIpv4(buffer, length)
            IP_VERSION_6 -> parseIpv6(buffer, length)
            else -> null
        }
    }

    private fun parseIpv4(buffer: ByteBuffer, length: Int): ParsedPacket? {
        if (length < 20) return null

        buffer.position(0)
        val versionAndIhl = buffer.get().toInt() and 0xFF
        val headerLength = (versionAndIhl and 0x0F) * 4

        if (length < headerLength) return null

        // Skip to protocol field (byte 9)
        buffer.position(9)
        val protocolNum = buffer.get().toInt() and 0xFF

        // Skip checksum, get source IP (bytes 12-15)
        buffer.position(12)
        val sourceIp = readIpv4Address(buffer)

        // Get destination IP (bytes 16-19)
        val destIp = readIpv4Address(buffer)

        val protocol = protocolFromNumber(protocolNum)

        // Parse transport layer header for ports
        val (sourcePort, destPort) = if (length > headerLength) {
            buffer.position(headerLength)
            parseTransportPorts(buffer, protocol, length - headerLength)
        } else {
            Pair(0, 0)
        }

        return ParsedPacket(
            protocol = protocol,
            sourceIp = sourceIp,
            sourcePort = sourcePort,
            destinationIp = destIp,
            destinationPort = destPort,
            packetLength = length,
        )
    }

    private fun parseIpv6(buffer: ByteBuffer, length: Int): ParsedPacket? {
        if (length < 40) return null // IPv6 fixed header size

        buffer.position(0)

        // Skip version/traffic class/flow label (4 bytes)
        buffer.position(4)

        // Payload length (2 bytes) - not currently used
        buffer.getShort()

        // Next header (protocol)
        val protocolNum = buffer.get().toInt() and 0xFF

        // Skip hop limit
        buffer.get()

        // Source IP (16 bytes)
        val sourceIp = readIpv6Address(buffer)

        // Destination IP (16 bytes)
        val destIp = readIpv6Address(buffer)

        val protocol = protocolFromNumber(protocolNum)

        // Parse transport layer header for ports (IPv6 header is always 40 bytes)
        val (sourcePort, destPort) = if (length > 40) {
            parseTransportPorts(buffer, protocol, length - 40)
        } else {
            Pair(0, 0)
        }

        return ParsedPacket(
            protocol = protocol,
            sourceIp = sourceIp,
            sourcePort = sourcePort,
            destinationIp = destIp,
            destinationPort = destPort,
            packetLength = length,
        )
    }

    private fun parseTransportPorts(buffer: ByteBuffer, protocol: Protocol, remaining: Int): Pair<Int, Int> {
        if (remaining < 4) return Pair(0, 0)

        return when (protocol) {
            Protocol.TCP, Protocol.UDP -> {
                val sourcePort = buffer.getShort().toInt() and 0xFFFF
                val destPort = buffer.getShort().toInt() and 0xFFFF
                Pair(sourcePort, destPort)
            }
            else -> Pair(0, 0)
        }
    }

    private fun readIpv4Address(buffer: ByteBuffer): String {
        val bytes = ByteArray(4)
        buffer.get(bytes)
        return bytes.joinToString(".") { (it.toInt() and 0xFF).toString() }
    }

    private fun readIpv6Address(buffer: ByteBuffer): String {
        val parts = mutableListOf<String>()
        repeat(8) {
            val part = buffer.getShort().toInt() and 0xFFFF
            parts.add(part.toString(16))
        }
        return parts.joinToString(":")
    }

    private fun protocolFromNumber(num: Int): Protocol {
        return when (num) {
            PROTOCOL_TCP -> Protocol.TCP
            PROTOCOL_UDP -> Protocol.UDP
            PROTOCOL_ICMP, PROTOCOL_ICMPV6 -> Protocol.ICMP
            else -> Protocol.OTHER
        }
    }
}
