package com.bearminds.bearguard.rules.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bearguard.composeapp.generated.resources.Res
import bearguard.composeapp.generated.resources.app_list_blocked
import bearguard.composeapp.generated.resources.app_list_empty
import bearguard.composeapp.generated.resources.app_list_internet_access
import bearguard.composeapp.generated.resources.app_list_mobile
import bearguard.composeapp.generated.resources.app_list_screen_off
import bearguard.composeapp.generated.resources.app_list_search_hint
import bearguard.composeapp.generated.resources.app_list_show_blocked
import bearguard.composeapp.generated.resources.app_list_show_system_apps
import bearguard.composeapp.generated.resources.app_list_title
import bearguard.composeapp.generated.resources.app_list_wifi
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppListScreen(
    viewModel: AppListViewModel = koinViewModel(),
) {
    val state by viewModel.viewState.collectAsState()

    val filteredApps = state.apps.filter { appWithRule ->
        val matchesSearch = state.searchQuery.isEmpty() ||
            appWithRule.app.name.contains(state.searchQuery, ignoreCase = true) ||
            appWithRule.app.packageName.contains(state.searchQuery, ignoreCase = true)
        val matchesBlockedFilter = !state.showBlockedOnly || !appWithRule.isAllowed
        matchesSearch && matchesBlockedFilter
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.app_list_title)) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search bar
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.onEvent(AppListContract.Event.UpdateSearchQuery(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = { Text(stringResource(Res.string.app_list_search_hint)) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FilterChip(
                    selected = state.showSystemApps,
                    onClick = {
                        viewModel.onEvent(AppListContract.Event.ToggleSystemApps(!state.showSystemApps))
                    },
                    label = { Text(stringResource(Res.string.app_list_show_system_apps)) },
                )
                FilterChip(
                    selected = state.showBlockedOnly,
                    onClick = {
                        viewModel.onEvent(AppListContract.Event.ToggleBlockedOnly(!state.showBlockedOnly))
                    },
                    label = { Text(stringResource(Res.string.app_list_show_blocked)) },
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // App list or loading/empty state
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                filteredApps.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(Res.string.app_list_empty),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(
                            items = filteredApps,
                            key = { it.app.packageName }
                        ) { appWithRule ->
                            AppListItem(
                                appWithRule = appWithRule,
                                onToggle = { isAllowed ->
                                    viewModel.onEvent(
                                        AppListContract.Event.ToggleAppAccess(
                                            packageName = appWithRule.app.packageName,
                                            isAllowed = isAllowed,
                                        )
                                    )
                                },
                                onWifiToggle = { allowWifi ->
                                    viewModel.onEvent(
                                        AppListContract.Event.ToggleWifiAccess(
                                            packageName = appWithRule.app.packageName,
                                            allowWifi = allowWifi,
                                        )
                                    )
                                },
                                onMobileToggle = { allowMobile ->
                                    viewModel.onEvent(
                                        AppListContract.Event.ToggleMobileAccess(
                                            packageName = appWithRule.app.packageName,
                                            allowMobile = allowMobile,
                                        )
                                    )
                                },
                                onScreenOffToggle = { allowWhenScreenOff ->
                                    viewModel.onEvent(
                                        AppListContract.Event.ToggleScreenOffAccess(
                                            packageName = appWithRule.app.packageName,
                                            allowWhenScreenOff = allowWhenScreenOff,
                                        )
                                    )
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AppListItem(
    appWithRule: AppWithRule,
    onToggle: (Boolean) -> Unit,
    onWifiToggle: (Boolean) -> Unit,
    onMobileToggle: (Boolean) -> Unit,
    onScreenOffToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (appWithRule.isAllowed) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            }
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Status icon
                Icon(
                    imageVector = if (appWithRule.isAllowed) {
                        Icons.Default.CheckCircle
                    } else {
                        Icons.Default.Block
                    },
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = if (appWithRule.isAllowed) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                )

                Spacer(modifier = Modifier.width(16.dp))

                // App info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = appWithRule.app.name,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        text = if (appWithRule.isAllowed) {
                            stringResource(Res.string.app_list_internet_access)
                        } else {
                            stringResource(Res.string.app_list_blocked)
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                // Main toggle switch
                Switch(
                    checked = appWithRule.isAllowed,
                    onCheckedChange = onToggle,
                )
            }

            // WiFi/Mobile/ScreenOff toggles - only shown when app is allowed
            if (appWithRule.isAllowed) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    // WiFi chip
                    FilterChip(
                        selected = appWithRule.allowWifi,
                        onClick = { onWifiToggle(!appWithRule.allowWifi) },
                        label = { Text(stringResource(Res.string.app_list_wifi)) },
                        modifier = Modifier.weight(1f),
                    )
                    // Mobile chip
                    FilterChip(
                        selected = appWithRule.allowMobile,
                        onClick = { onMobileToggle(!appWithRule.allowMobile) },
                        label = { Text(stringResource(Res.string.app_list_mobile)) },
                        modifier = Modifier.weight(1f),
                    )
                    // Screen off chip
                    FilterChip(
                        selected = appWithRule.allowWhenScreenOff,
                        onClick = { onScreenOffToggle(!appWithRule.allowWhenScreenOff) },
                        label = { Text(stringResource(Res.string.app_list_screen_off)) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun AppListItemPreview() {
    AppListItem(
        appWithRule = AppWithRule(
            app = com.bearminds.bearguard.rules.model.AppInfo(
                packageName = "com.example.app",
                name = "Example App",
                isSystemApp = false,
                uid = 1000,
            ),
            isAllowed = true,
            allowWifi = true,
            allowMobile = false,
            allowWhenScreenOff = true,
        ),
        onToggle = {},
        onWifiToggle = {},
        onMobileToggle = {},
        onScreenOffToggle = {},
    )
}
