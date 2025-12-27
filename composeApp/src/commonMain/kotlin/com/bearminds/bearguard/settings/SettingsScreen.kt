package com.bearminds.bearguard.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import bearguard.composeapp.generated.resources.Res
import bearguard.composeapp.generated.resources.nav_settings
import bearguard.composeapp.generated.resources.settings_appearance
import bearguard.composeapp.generated.resources.settings_behavior
import bearguard.composeapp.generated.resources.settings_default_rule
import bearguard.composeapp.generated.resources.settings_default_rule_allow
import bearguard.composeapp.generated.resources.settings_default_rule_block
import bearguard.composeapp.generated.resources.settings_auto_start
import bearguard.composeapp.generated.resources.settings_auto_start_description
import bearguard.composeapp.generated.resources.settings_lockdown_mode
import bearguard.composeapp.generated.resources.settings_lockdown_mode_description
import bearguard.composeapp.generated.resources.settings_security
import bearguard.composeapp.generated.resources.settings_startup
import bearguard.composeapp.generated.resources.settings_show_system_apps
import bearguard.composeapp.generated.resources.settings_theme
import bearguard.composeapp.generated.resources.settings_theme_dark
import bearguard.composeapp.generated.resources.settings_theme_light
import bearguard.composeapp.generated.resources.settings_theme_system
import com.bearminds.bearguard.settings.SettingsContract.DefaultRule
import com.bearminds.bearguard.settings.SettingsContract.ThemeMode
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val state by viewModel.viewState.collectAsState()

    SettingsScreenContent(
        state = state,
        onEvent = viewModel::onEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreenContent(
    state: SettingsContract.State,
    onEvent: (SettingsContract.Event) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.nav_settings)) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Appearance Section
            SectionHeader(title = stringResource(Res.string.settings_appearance))

            ThemeSelector(
                selectedMode = state.themeMode,
                onModeSelected = { onEvent(SettingsContract.Event.SetThemeMode(it)) },
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Behavior Section
            SectionHeader(title = stringResource(Res.string.settings_behavior))

            DefaultRuleSelector(
                selectedRule = state.defaultRuleForNewApps,
                onRuleSelected = { onEvent(SettingsContract.Event.SetDefaultRuleForNewApps(it)) },
            )

            SwitchPreference(
                title = stringResource(Res.string.settings_show_system_apps),
                checked = state.showSystemAppsByDefault,
                onCheckedChange = { onEvent(SettingsContract.Event.SetShowSystemAppsByDefault(it)) },
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Security Section
            SectionHeader(title = stringResource(Res.string.settings_security))

            SwitchPreferenceWithDescription(
                title = stringResource(Res.string.settings_lockdown_mode),
                description = stringResource(Res.string.settings_lockdown_mode_description),
                checked = state.lockdownMode,
                onCheckedChange = { onEvent(SettingsContract.Event.SetLockdownMode(it)) },
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Startup Section
            SectionHeader(title = stringResource(Res.string.settings_startup))

            SwitchPreferenceWithDescription(
                title = stringResource(Res.string.settings_auto_start),
                description = stringResource(Res.string.settings_auto_start_description),
                checked = state.autoStartOnBoot,
                onCheckedChange = { onEvent(SettingsContract.Event.SetAutoStartOnBoot(it)) },
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
    )
}

@Composable
private fun ThemeSelector(
    selectedMode: ThemeMode,
    onModeSelected: (ThemeMode) -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = stringResource(Res.string.settings_theme),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Column(modifier = Modifier.selectableGroup()) {
            ThemeOption(
                labelRes = Res.string.settings_theme_light,
                selected = selectedMode == ThemeMode.LIGHT,
                onClick = { onModeSelected(ThemeMode.LIGHT) },
            )
            ThemeOption(
                labelRes = Res.string.settings_theme_dark,
                selected = selectedMode == ThemeMode.DARK,
                onClick = { onModeSelected(ThemeMode.DARK) },
            )
            ThemeOption(
                labelRes = Res.string.settings_theme_system,
                selected = selectedMode == ThemeMode.SYSTEM,
                onClick = { onModeSelected(ThemeMode.SYSTEM) },
            )
        }
    }
}

@Composable
private fun ThemeOption(
    labelRes: StringResource,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton,
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
        )
        Text(
            text = stringResource(labelRes),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}

@Composable
private fun DefaultRuleSelector(
    selectedRule: DefaultRule,
    onRuleSelected: (DefaultRule) -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = stringResource(Res.string.settings_default_rule),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Column(modifier = Modifier.selectableGroup()) {
            DefaultRuleOption(
                labelRes = Res.string.settings_default_rule_allow,
                selected = selectedRule == DefaultRule.ALLOW,
                onClick = { onRuleSelected(DefaultRule.ALLOW) },
            )
            DefaultRuleOption(
                labelRes = Res.string.settings_default_rule_block,
                selected = selectedRule == DefaultRule.BLOCK,
                onClick = { onRuleSelected(DefaultRule.BLOCK) },
            )
        }
    }
}

@Composable
private fun DefaultRuleOption(
    labelRes: StringResource,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton,
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
        )
        Text(
            text = stringResource(labelRes),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}

@Composable
private fun SwitchPreference(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        Switch(
            checked = checked,
            onCheckedChange = null,
        )
    }
}

@Composable
private fun SwitchPreferenceWithDescription(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = null,
        )
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    SettingsScreenContent(
        state = SettingsContract.State(),
        onEvent = {},
    )
}
