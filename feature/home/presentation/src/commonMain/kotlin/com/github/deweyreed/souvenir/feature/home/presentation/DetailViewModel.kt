package com.github.deweyreed.souvenir.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.deweyreed.souvenir.feature.home.api.ArticleEntity
import com.github.deweyreed.souvenir.feature.home.api.ArticleRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal data class DetailUiState(
    val article: ArticleEntity? = null,
)

@Inject
@ViewModelKey
@ContributesIntoMap(AppScope::class)
internal class DetailViewModel(private val repository: ArticleRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()
    private var loadJob: Job? = null
    private var loadedArticleId: Long? = null

    fun load(id: Long) {
        if (loadJob != null && loadedArticleId == id) return
        loadJob?.cancel()
        loadedArticleId = id
        loadJob = viewModelScope.launch {
            repository.getItemFlow(id).collectLatest { item ->
                _uiState.update {
                    it.copy(article = item)
                }
            }
        }
    }
}
