package com.github.deweyreed.souvenir.feature.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.deweyreed.souvenir.base.api.AppTheme
import com.github.deweyreed.souvenir.base.api.Qualifiers
import com.github.deweyreed.souvenir.base.api.Settings
import com.github.deweyreed.souvenir.base.api.getAppThemeFlow
import com.github.deweyreed.souvenir.base.api.setAppTheme
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Inject
@ViewModelKey(SettingsViewModel::class)
@ContributesIntoMap(AppScope::class)
class SettingsViewModel(
    @Qualifiers.Dispatchers.Io private val ioDispatcher: CoroutineDispatcher,
    private val settings: Settings,
) : ViewModel() {
    data class Screen(
        val theme: AppTheme = AppTheme.SYSTEM,
    )

    sealed interface Action {
        data class SetTheme(val theme: AppTheme) : Action
    }

    private val _screen = MutableStateFlow(Screen())
    val screen: StateFlow<Screen> = _screen.asStateFlow()
    private var loadJob: Job? = null

    fun load() {
        if (loadJob != null) return
        loadJob = viewModelScope.launch(ioDispatcher) {
            settings.getAppThemeFlow().collectLatest { theme ->
                _screen.update { it.copy(theme = theme) }
            }
        }
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.SetTheme -> {
                viewModelScope.launch {
                    settings.setAppTheme(action.theme)
                }
            }
        }
    }
}
