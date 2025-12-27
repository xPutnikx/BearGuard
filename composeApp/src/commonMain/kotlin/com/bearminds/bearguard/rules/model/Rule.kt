package com.bearminds.bearguard.rules.model

import kotlinx.serialization.Serializable

/**
 * Represents a firewall rule for an application.
 *
 * @param packageName The app's package identifier
 * @param isAllowed Whether network access is allowed for this app
 * @param allowWifi Whether WiFi access is allowed (when isAllowed is true)
 * @param allowMobileData Whether mobile data access is allowed (when isAllowed is true)
 * @param allowWhenScreenOff Whether access is allowed when screen is off (battery saving)
 */
@Serializable
data class Rule(
    val packageName: String,
    val isAllowed: Boolean = true,
    val allowWifi: Boolean = true,
    val allowMobileData: Boolean = true,
    val allowWhenScreenOff: Boolean = true,
)
