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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal data class HomeUiState(
    val articles: List<ArticleEntity>? = null,
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
