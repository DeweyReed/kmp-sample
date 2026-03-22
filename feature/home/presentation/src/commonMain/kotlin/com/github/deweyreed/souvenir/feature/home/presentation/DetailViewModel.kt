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

@Inject
@ViewModelKey(DetailViewModel::class)
@ContributesIntoMap(AppScope::class)
class DetailViewModel(private val repository: ArticleRepository) : ViewModel() {
    data class Screen(
        val article: ArticleEntity? = null,
    )

    private val _screen: MutableStateFlow<Screen> = MutableStateFlow(Screen())
    val screen: StateFlow<Screen> = _screen.asStateFlow()
    private var loadJob: Job? = null

    fun load(id: Long) {
        if (loadJob != null && _screen.value.article?.id == id) return
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            repository.getItemFlow(id).collectLatest { item ->
                _screen.update {
                    it.copy(article = item)
                }
            }
        }
    }
}
