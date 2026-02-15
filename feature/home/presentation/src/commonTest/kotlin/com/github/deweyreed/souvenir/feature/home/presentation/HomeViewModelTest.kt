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
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeArticleRepository
    private lateinit var viewModel: HomeViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeArticleRepository()
        viewModel = HomeViewModel(repository)
    }

    @Test
    fun `load should clear items and fetch pagination`() = runTest {
        viewModel.load()
        advanceUntilIdle()

        assertTrue(repository.clearItemsCalled)
        assertEquals(emptyList(), viewModel.screen.value.articles)

        val items = listOf(
            ArticleEntity(1L, "Title 1", "Image 1", "Summary 1"),
            ArticleEntity(2L, "Title 2", "Image 2", "Summary 2")
        )
        repository.itemsFlow.value = items
        advanceUntilIdle()

        assertEquals(items, viewModel.screen.value.articles)
    }

    @Test
    fun `load should only run once`() = runTest {
        viewModel.load()
        viewModel.load()
        advanceUntilIdle()

        assertEquals(1, repository.clearItemsCallCount)
    }

    @Test
    fun `onAction LoadMoreItems should call loadMore on pagination`() = runTest {
        viewModel.load()
        advanceUntilIdle()

        viewModel.onAction(HomeViewModel.Action.LoadMoreItems)
        advanceUntilIdle()

        assertTrue(repository.loadMoreCalled)
    }

    private class FakeArticleRepository : ArticleRepository {
        val itemsFlow =
            MutableStateFlow<List<ArticleEntity>>(emptyList())
        var clearItemsCalled = false
        var clearItemsCallCount = 0
        var loadMoreCalled = false

        override fun getItemsPagination(): Pagination<ArticleEntity> {
            return Pagination(
                flow = itemsFlow,
                loadMore = { loadMoreCalled = true }
            )
        }

        override fun getItemFlow(id: Long): Flow<ArticleEntity?> {
            error("Not used")
        }

        override suspend fun clearItems() {
            clearItemsCalled = true
            clearItemsCallCount++
        }
    }
}
