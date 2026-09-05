package com.github.deweyreed.souvenir.feature.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.deweyreed.souvenir.base.api.AppTheme
import com.github.deweyreed.souvenir.base.api.Settings
import com.github.deweyreed.souvenir.base.api.getAppThemeFlow
import com.github.deweyreed.souvenir.base.api.setAppTheme
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal data class SettingsUiState(
    val theme: AppTheme = AppTheme.SYSTEM,
)

internal sealed interface SettingsAction {
    data class SetTheme(val theme: AppTheme) : SettingsAction
}

@Inject
@ViewModelKey
@ContributesIntoMap(AppScope::class)
internal class SettingsViewModel(
    private val settings: Settings,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    private var loadJob: Job? = null

    fun load() {
        if (loadJob != null) return
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            coroutineScope {
                launch {
                    settings.getAppThemeFlow().collectLatest { theme ->
                        _uiState.update { it.copy(theme = theme) }
                    }
                }
            }
        }
    }

    fun onAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.SetTheme -> {
                viewModelScope.launch {
                    settings.setAppTheme(action.theme)
                }
            }
        }
    }
}
