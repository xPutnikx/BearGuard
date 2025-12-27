package com.bearminds.bearguard

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import bearguard.composeapp.generated.resources.Res
import bearguard.composeapp.generated.resources.nav_apps
import bearguard.composeapp.generated.resources.nav_home
import bearguard.composeapp.generated.resources.nav_settings
import bearguard.composeapp.generated.resources.nav_traffic
import com.bearminds.bearguard.home.HomeScreen
import com.bearminds.bearguard.navigation.NavRoutes
import com.bearminds.bearguard.rules.ui.AppListScreen
import com.bearminds.bearguard.settings.SettingsContract.ThemeMode
import com.bearminds.bearguard.settings.SettingsScreen
import com.bearminds.bearguard.settings.data.SettingsRepository
import com.bearminds.bearguard.traffic.TrafficScreen
import com.bearminds.theme.AppTheme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun App() {
    val settingsRepository: SettingsRepository = koinInject()
    val themeMode by settingsRepository.observeThemeMode().collectAsState(initial = ThemeMode.SYSTEM)
    val isSystemDark = isSystemInDarkTheme()

    val isDarkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemDark
    }

    AppTheme(isDarkTheme = isDarkTheme) {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        Scaffold(
            bottomBar = {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.hasRoute(item.route::class)
                        } == true

                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = null) },
                            label = { Text(stringResource(item.label)) },
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = NavRoutes.Home,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable<NavRoutes.Home> {
                    HomeScreen()
                }
                composable<NavRoutes.Apps> {
                    AppListScreen()
                }
                composable<NavRoutes.Traffic> {
                    TrafficScreen()
                }
                composable<NavRoutes.Settings> {
                    SettingsScreen()
                }
            }
        }
    }
}

private data class BottomNavItem(
    val route: NavRoutes,
    val icon: ImageVector,
    val label: StringResource,
)

private val bottomNavItems = listOf(
    BottomNavItem(NavRoutes.Home, Icons.Default.Home, Res.string.nav_home),
    BottomNavItem(NavRoutes.Apps, Icons.Default.Apps, Res.string.nav_apps),
    BottomNavItem(NavRoutes.Traffic, Icons.Default.Timeline, Res.string.nav_traffic),
    BottomNavItem(NavRoutes.Settings, Icons.Default.Settings, Res.string.nav_settings),
)
