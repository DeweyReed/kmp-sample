package com.github.deweyreed.souvenir.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.deweyreed.souvenir.base.api.Pagination
import com.github.deweyreed.souvenir.feature.home.api.ArticleEntity
import com.github.deweyreed.souvenir.feature.home.api.ArticleRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

internal data class HomeUiState(
    val articles: List<ArticleEntity>? = null,
    val isInitialContentReady: Boolean = false,
)

internal sealed interface HomeAction {
    data object LoadMoreItems : HomeAction
}

@Inject
@ViewModelKey
@ContributesIntoMap(AppScope::class)
internal class HomeViewModel(private val repository: ArticleRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    private var loadJob: Job? = null
    private var itemsPagination: Pagination<ArticleEntity>? = null

    fun load() {
        if (loadJob != null) return
        loadJob = viewModelScope.launch {
            coroutineScope {
                launch {
                    // TODO: A better paging impl
                    // Avoid briefly showing cached content immediately before refreshed content.
                    delay(1.seconds)
                    _uiState.update {
                        it.copy(isInitialContentReady = true)
                    }
                }
                launch {
                    repository.refreshItems()
                }
                launch {
                    val pagination = repository.getItemsPagination()
                    itemsPagination = pagination
                    pagination.flow.collectLatest { items ->
                        _uiState.update {
                            it.copy(articles = items)
                        }
                    }
                }
            }
        }
    }

    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.LoadMoreItems -> {
                viewModelScope.launch {
                    itemsPagination?.loadMore?.invoke()
                }
            }
        }
    }
}
