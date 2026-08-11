package com.github.deweyreed.souvenir

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.deweyreed.souvenir.base.api.AppTheme
import com.github.deweyreed.souvenir.base.api.Qualifiers
import com.github.deweyreed.souvenir.base.api.Settings
import com.github.deweyreed.souvenir.base.api.getAppThemeFlow
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AppUiState(
    val theme: AppTheme = AppTheme.SYSTEM,
)

@Inject
@ViewModelKey
@ContributesIntoMap(AppScope::class)
class AppViewModel(
    @Qualifiers.Dispatchers.Io private val ioDispatcher: CoroutineDispatcher,
    private val settings: Settings,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()
    private var loadJob: Job? = null

    fun load() {
        if (loadJob?.isActive == true) return
        loadJob = viewModelScope.launch(ioDispatcher) {
            coroutineScope {
                launch {
                    settings.getAppThemeFlow().collectLatest { theme ->
                        _uiState.update { it.copy(theme = theme) }
                    }
                }
            }
        }
    }
}
