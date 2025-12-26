package com.github.deweyreed.souvenir.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.deweyreed.souvenir.feature.home.api.ArticleEntity
import com.github.deweyreed.souvenir.feature.home.api.ArticleRepository
import com.github.deweyreed.souvenir.feature.home.data.ArticleRepositoryImpl
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class HomeViewModel : ViewModel() {
    data class Screen(
        val articles: List<ArticleEntity>? = null,
    )

    private val _screen: MutableStateFlow<Screen> = MutableStateFlow(Screen())
    val screen: StateFlow<Screen> = _screen.asStateFlow()
    private var loadJob: Job? = null

    private val repository: ArticleRepository = ArticleRepositoryImpl()

    fun load() {
        if (loadJob != null) return
        loadJob = viewModelScope.launch {
            val result = repository.getArticles()
            if (result.isSuccess) {
                _screen.value = _screen.value.copy(articles = result.getOrNull() ?: emptyList())
            }
        }
    }
}
