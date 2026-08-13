package com.github.deweyreed.souvenir.feature.home.presentation

import com.github.deweyreed.souvenir.base.api.Pagination
import com.github.deweyreed.souvenir.feature.home.api.ArticleEntity
import com.github.deweyreed.souvenir.feature.home.api.ArticleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeArticleRepository
    private lateinit var viewModel: DetailViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeArticleRepository()
        viewModel = DetailViewModel(repository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `load should update ui state with article`() = runTest {
        val article = ArticleEntity(1L, "Title", "Image", "Summary")
        repository.articleFlow(1L).value = article

        viewModel.load(1L)
        advanceUntilIdle()

        assertEquals(article, viewModel.uiState.value.article)
    }

    @Test
    fun `load with different id should switch observed article flow`() = runTest {
        val article1 = ArticleEntity(1L, "Title 1", "Image 1", "Summary 1")
        val article2 = ArticleEntity(2L, "Title 2", "Image 2", "Summary 2")

        repository.articleFlow(1L).value = article1
        viewModel.load(1L)
        advanceUntilIdle()
        assertEquals(article1, viewModel.uiState.value.article)

        repository.articleFlow(2L).value = article2
        viewModel.load(2L)
        advanceUntilIdle()
        assertEquals(article2, viewModel.uiState.value.article)

        repository.articleFlow(1L).value = article1.copy(title = "Stale title")
        advanceUntilIdle()

        assertEquals(article2, viewModel.uiState.value.article)
        assertEquals(listOf(1L, 2L), repository.requestedIds)
    }

    @Test
    fun `load should handle null items`() = runTest {
        viewModel.load(1L)
        repository.articleFlow(1L).value = null
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.article)
    }

    @Test
    fun `load with same id should keep a single observation`() = runTest {
        viewModel.load(1L)
        viewModel.load(1L)
        advanceUntilIdle()

        assertEquals(listOf(1L), repository.requestedIds)
    }

    private class FakeArticleRepository : ArticleRepository {
        private val articleFlows =
            mutableMapOf<Long, MutableStateFlow<ArticleEntity?>>()
        val requestedIds = mutableListOf<Long>()

        fun articleFlow(id: Long): MutableStateFlow<ArticleEntity?> =
            articleFlows.getOrPut(id) { MutableStateFlow(null) }

        override fun getItemFlow(id: Long): Flow<ArticleEntity?> {
            requestedIds += id
            return articleFlow(id)
        }

        override fun getItemsPagination(): Pagination<ArticleEntity> {
            error("Not used")
        }

        override suspend fun clearItems() {
            error("Not used")
        }
    }
}
