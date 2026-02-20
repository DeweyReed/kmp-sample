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
    fun `load should update screen with article`() = runTest {
        val article = ArticleEntity(1L, "Title", "Image", "Summary")
        repository.articleFlow.value = article

        viewModel.load(1L)
        advanceUntilIdle()

        assertEquals(article, viewModel.screen.value.article)
    }

    @Test
    fun `load with different id should update screen`() = runTest {
        val article1 = ArticleEntity(1L, "Title 1", "Image 1", "Summary 1")
        val article2 = ArticleEntity(2L, "Title 2", "Image 2", "Summary 2")

        viewModel.load(1L)
        repository.articleFlow.value = article1
        advanceUntilIdle()
        assertEquals(article1, viewModel.screen.value.article)

        viewModel.load(2L)
        repository.articleFlow.value = article2
        advanceUntilIdle()
        assertEquals(article2, viewModel.screen.value.article)
    }

    @Test
    fun `load should handle null items`() = runTest {
        viewModel.load(1L)
        repository.articleFlow.value = null
        advanceUntilIdle()

        assertNull(viewModel.screen.value.article)
    }

    private class FakeArticleRepository : ArticleRepository {
        val articleFlow = MutableStateFlow<ArticleEntity?>(null)

        override fun getItemFlow(id: Long): Flow<ArticleEntity?> = articleFlow

        override fun getItemsPagination(): Pagination<ArticleEntity> {
            error("Not used")
        }

        override suspend fun clearItems() {
            error("Not used")
        }
    }
}
