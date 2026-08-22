package com.github.deweyreed.souvenir.feature.home.presentation

import com.github.deweyreed.souvenir.base.api.Pagination
import com.github.deweyreed.souvenir.feature.home.api.ArticleEntity
import com.github.deweyreed.souvenir.feature.home.api.ArticleRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds

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

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `load collects pagination items`() = runTest {
        viewModel.load()
        advanceUntilIdle()

        val items = listOf(
            ArticleEntity(1L, "Title 1", "Image 1", "Summary 1"),
            ArticleEntity(2L, "Title 2", "Image 2", "Summary 2")
        )
        repository.itemsFlow.value = items
        advanceUntilIdle()

        assertEquals(items, viewModel.uiState.value.articles)
    }

    @Test
    fun `cached items become ready after initial wait while refresh is pending`() = runTest {
        val cachedItems = listOf(
            ArticleEntity(1L, "Cached", "Cached image", "Cached summary"),
        )
        repository.itemsFlow.value = cachedItems
        repository.suspendRefresh = true

        viewModel.load()
        runCurrent()

        try {
            assertTrue(repository.refreshStarted.isCompleted)
            assertEquals(cachedItems, viewModel.uiState.value.articles)
            assertFalse(viewModel.uiState.value.isInitialContentReady)

            advanceTimeBy(999.milliseconds)
            runCurrent()
            assertFalse(viewModel.uiState.value.isInitialContentReady)

            advanceTimeBy(1.milliseconds)
            runCurrent()
            assertTrue(viewModel.uiState.value.isInitialContentReady)
        } finally {
            repository.finishRefresh.complete(Unit)
        }
        advanceUntilIdle()
    }

    @Test
    fun `load refreshes only once`() = runTest {
        viewModel.load()
        viewModel.load()
        advanceUntilIdle()

        assertEquals(1, repository.refreshItemsCallCount)
    }

    @Test
    fun `onAction LoadMoreItems should call loadMore on pagination`() = runTest {
        viewModel.load()
        advanceUntilIdle()

        viewModel.onAction(HomeAction.LoadMoreItems)
        advanceUntilIdle()

        assertTrue(repository.loadMoreCalled)
    }

    private class FakeArticleRepository : ArticleRepository {
        val itemsFlow =
            MutableStateFlow<List<ArticleEntity>>(emptyList())
        var refreshItemsCallCount = 0
        var suspendRefresh = false
        val refreshStarted = CompletableDeferred<Unit>()
        val finishRefresh = CompletableDeferred<Unit>()
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

        override suspend fun refreshItems() {
            refreshItemsCallCount++
            refreshStarted.complete(Unit)
            if (suspendRefresh) {
                finishRefresh.await()
            }
        }
    }
}
