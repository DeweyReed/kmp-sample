package com.github.deweyreed.souvenir.feature.home.presentation

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.deweyreed.souvenir.base.api.Pagination
import com.github.deweyreed.souvenir.feature.home.api.ArticleEntity
import com.github.deweyreed.souvenir.feature.home.api.ArticleRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class HomeViewModel(private val repository: ArticleRepository) : ViewModel() {
    data class Screen(
        val articles: List<ArticleEntity>? = null,
    )

    @Immutable
    sealed interface Action {
        data object LoadMoreItems : Action
    }

    private val _screen: MutableStateFlow<Screen> = MutableStateFlow(Screen())
    val screen: StateFlow<Screen> = _screen.asStateFlow()
    private var loadJob: Job? = null
    private var itemsPagination: Pagination<ArticleEntity>? = null

    fun load() {
        if (loadJob != null) return
        loadJob = viewModelScope.launch {
            val pagination =
                repository.getItemsPagination(coroutineScope = viewModelScope)
            itemsPagination = pagination
            pagination.flow.collectLatest { items ->
                _screen.update {
                    it.copy(articles = items)
                }
            }
        }
    }

    fun onAction(action: Action) {
        when (action) {
            Action.LoadMoreItems -> itemsPagination?.loadMore?.invoke()
        }
    }
}
