package com.github.deweyreed.souvenir.feature.settings.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.deweyreed.souvenir.base.api.AppTheme
import dev.zacsweers.metrox.viewmodel.metroViewModel

@Composable
internal fun SettingsScreen(
    onLibraries: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues.Zero,
) {
    val viewModel = metroViewModel<SettingsViewModel>()
    LaunchedEffect(Unit) { viewModel.load() }
    val screen by viewModel.screen.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
    ) {
        item {
            var showThemeDialog by remember { mutableStateOf(false) }
            ListItem(
                headlineContent = { Text("Theme") },
                supportingContent = { Text(screen.theme.name) },
                modifier = Modifier.clickable { showThemeDialog = true },
            )
            if (showThemeDialog) {
                ThemeDialog(
                    currentTheme = screen.theme,
                    onThemeSelected = {
                        showThemeDialog = false
                        viewModel.onAction(SettingsViewModel.Action.SetTheme(it))
                    },
                    onDismiss = { showThemeDialog = false },
                )
            }
        }
        item {
            ListItem(
                headlineContent = { Text("Licenses") },
                modifier = Modifier.clickable(onClick = onLibraries),
            )
        }
    }
}

@Composable
private fun ThemeDialog(
    currentTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Theme") },
        text = {
            Column {
                AppTheme.entries.forEach { theme ->
                    ListItem(
                        headlineContent = { Text(theme.name) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = theme == currentTheme,
                                role = Role.RadioButton,
                                onClick = { onThemeSelected(theme) },
                            ),
                        leadingContent = {
                            RadioButton(selected = theme == currentTheme, onClick = null)
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}
