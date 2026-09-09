@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.deweyreed.souvenir.feature.settings.presentation

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.deweyreed.souvenir.base.api.AppTheme
import dev.zacsweers.metrox.viewmodel.metroViewModel
import org.jetbrains.compose.resources.painterResource
import souvenir.base.presentation.generated.resources.Res
import souvenir.base.presentation.generated.resources.back

@Composable
fun Settings(
    onBack: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    modifier: Modifier = Modifier,
) {
    val viewModel = metroViewModel<SettingsViewModel>()
    SideEffect(viewModel) { viewModel.load() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SettingsUi(
        uiState = uiState,
        onAction = viewModel::onAction,
        onBack = onBack,
        sharedTransitionScope = sharedTransitionScope,
        modifier = modifier,
    )
}

@Composable
private fun SettingsUi(
    uiState: SettingsUiState,
    onAction: (SettingsAction) -> Unit,
    onBack: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            sharedTransitionScope.run {
                TopAppBar(
                    title = { Text("Settings") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                painter = painterResource(Res.drawable.back),
                                contentDescription = "Back",
                            )
                        }
                    },
                    modifier = Modifier.renderInSharedTransitionScopeOverlay(
                        zIndexInOverlay = 1f,
                    ),
                )
            }
        },
    ) { padding ->
        SettingsContent(
            uiState = uiState,
            onAction = onAction,
            contentPadding = padding,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun SettingsContent(
    uiState: SettingsUiState,
    onAction: (SettingsAction) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues.Zero,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
    ) {
        item {
            var showThemeDialog by rememberSaveable { mutableStateOf(false) }
            ListItem(
                headlineContent = { Text("Theme") },
                supportingContent = { Text(uiState.theme.name) },
                modifier = Modifier.clickable { showThemeDialog = true },
            )
            if (showThemeDialog) {
                ThemeDialog(
                    currentTheme = uiState.theme,
                    onThemeSelected = { onAction(SettingsAction.SetTheme(it)) },
                    onDismiss = { showThemeDialog = false },
                )
            }
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
