package com.bearminds.bearguard.navigation

import kotlinx.serialization.Serializable

/**
 * Navigation routes for the app.
 */
sealed interface NavRoutes {

    @Serializable
    data object Home : NavRoutes

    @Serializable
    data object Apps : NavRoutes

    @Serializable
    data object Traffic : NavRoutes

    @Serializable
    data object Settings : NavRoutes
}
