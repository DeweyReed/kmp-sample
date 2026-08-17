package com.github.deweyreed.souvenir.feature.home.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ArticleRepositoryImplTest {
    private val dao = FakeArticleDao()
    private val requestedUrls = mutableListOf<String>()

    @Test
    fun `refreshItems replaces stored items with the first page`() = runTest {
        dao.itemsFlow.value = listOf(articleData(99L))
        dao.nextPage = "$API/articles?offset=99"
        val repository = createRepository {
            respondJson(pageJson(ids = listOf(1L, 2L), next = "$API/articles?offset=2"))
        }

        repository.refreshItems()

        assertEquals(listOf("$API/articles"), requestedUrls)
        assertEquals(listOf(1L, 2L), dao.itemsFlow.value.map { it.id })
        assertEquals("Title 1", dao.itemsFlow.value.first().title)
        assertEquals("$API/articles?offset=2", dao.nextPage)
        assertEquals(0, dao.appendPageCallCount)
    }

    @Test
    fun `refreshItems keeps stored items when the request fails`() = runTest {
        val cachedItems = listOf(articleData(99L))
        dao.itemsFlow.value = cachedItems
        dao.nextPage = "$API/articles?offset=99"
        val repository = createRepository {
            respondError(HttpStatusCode.InternalServerError)
        }

        repository.refreshItems()

        assertEquals(cachedItems, dao.itemsFlow.value)
        assertEquals("$API/articles?offset=99", dao.nextPage)
        assertEquals(0, dao.replacePageCallCount)
    }

    @Test
    fun `loadMore appends the stored next page`() = runTest {
        dao.itemsFlow.value = listOf(articleData(1L))
        dao.nextPage = "$API/articles?offset=1"
        val repository = createRepository {
            respondJson(pageJson(ids = listOf(2L), next = null))
        }

        repository.getItemsPagination().loadMore()

        assertEquals(listOf("$API/articles?offset=1"), requestedUrls)
        assertEquals(listOf(1L, 2L), dao.itemsFlow.value.map { it.id })
        assertNull(dao.nextPage)
        assertEquals(0, dao.replacePageCallCount)
    }

    @Test
    fun `loadMore does nothing when there is no next page`() = runTest {
        dao.itemsFlow.value = listOf(articleData(1L))
        dao.nextPage = null
        val repository = createRepository {
            error("No request is expected")
        }

        repository.getItemsPagination().loadMore()

        assertTrue(requestedUrls.isEmpty())
        assertEquals(listOf(1L), dao.itemsFlow.value.map { it.id })
        assertEquals(0, dao.appendPageCallCount)
    }

    @Test
    fun `loadMore ignores a call while another one is in flight`() = runTest {
        dao.nextPage = "$API/articles?offset=1"
        val requestStarted = CompletableDeferred<Unit>()
        val finishRequest = CompletableDeferred<Unit>()
        val repository = createRepository {
            requestStarted.complete(Unit)
            finishRequest.await()
            respondJson(pageJson(ids = listOf(2L), next = null))
        }
        val pagination = repository.getItemsPagination()

        val inFlight = launch { pagination.loadMore() }
        requestStarted.await()

        pagination.loadMore()

        assertEquals(1, requestedUrls.size)
        assertEquals(0, dao.appendPageCallCount)

        finishRequest.complete(Unit)
        inFlight.join()

        assertEquals(1, requestedUrls.size)
        assertEquals(1, dao.appendPageCallCount)
    }

    @Test
    fun `loadMore works again after a cancelled call`() = runTest {
        dao.nextPage = "$API/articles?offset=1"
        val requestStarted = CompletableDeferred<Unit>()
        val neverFinish = CompletableDeferred<Unit>()
        var firstRequest = true
        val repository = createRepository {
            if (firstRequest) {
                firstRequest = false
                requestStarted.complete(Unit)
                neverFinish.await()
            }
            respondJson(pageJson(ids = listOf(2L), next = null))
        }
        val pagination = repository.getItemsPagination()

        val cancelled = launch { pagination.loadMore() }
        requestStarted.await()
        cancelled.cancelAndJoin()

        assertEquals(0, dao.appendPageCallCount)

        pagination.loadMore()

        assertEquals(2, requestedUrls.size)
        assertEquals(1, dao.appendPageCallCount)
        assertEquals(listOf(2L), dao.itemsFlow.value.map { it.id })

        neverFinish.complete(Unit)
    }

    private fun TestScope.createRepository(
        handler: suspend MockRequestHandleScope.() -> HttpResponseData,
    ): ArticleRepositoryImpl {
        val recordingHandler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData =
            { request ->
                requestedUrls += request.url.toString()
                handler()
            }
        return ArticleRepositoryImpl(
            ioDispatcher = StandardTestDispatcher(testScheduler),
            dao = dao,
            httpClient = HttpClient(MockEngine(recordingHandler)) {
                defaultRequest {
                    url(BASE_URL)
                }
                install(ContentNegotiation) {
                    json(
                        Json {
                            ignoreUnknownKeys = true
                        }
                    )
                }
            },
        )
    }

    private fun MockRequestHandleScope.respondJson(body: String): HttpResponseData {
        return respond(
            content = body,
            status = HttpStatusCode.OK,
            headers = headersOf(
                name = HttpHeaders.ContentType,
                value = ContentType.Application.Json.toString(),
            ),
        )
    }

    private fun pageJson(ids: List<Long>, next: String?): String {
        val results = ids.joinToString(separator = ",") { id ->
            """
            {
                "id": $id,
                "title": "Title $id",
                "authors": [],
                "url": "https://example.com/$id",
                "image_url": "https://example.com/$id.png",
                "news_site": "Example",
                "summary": "Summary $id",
                "published_at": "2026-01-01T00:00:00Z",
                "updated_at": "2026-01-02T00:00:00Z",
                "featured": false
            }
            """.trimIndent()
        }
        val nextValue = next?.let { "\"$it\"" } ?: "null"
        return """
        {
            "count": ${ids.size},
            "next": $nextValue,
            "previous": null,
            "results": [$results]
        }
        """.trimIndent()
    }

    private fun articleData(id: Long): ArticleData {
        return ArticleData(
            id = id,
            title = "Cached $id",
            url = "https://example.com/$id",
            imageUrl = "https://example.com/$id.png",
            summary = "Cached summary $id",
            publishedAt = "2025-01-01T00:00:00Z",
            updatedAt = "2025-01-02T00:00:00Z",
        )
    }

    private class FakeArticleDao : ArticleDao {
        val itemsFlow =
            MutableStateFlow<List<ArticleData>>(emptyList())
        var nextPage: String? = null
        var replacePageCallCount = 0
        var appendPageCallCount = 0

        override fun getItemsFlow(): Flow<List<ArticleData>> {
            return itemsFlow
        }

        override suspend fun getItemCount(): Int {
            return itemsFlow.value.size
        }

        override fun getItemFlow(id: Long): Flow<ArticleData?> {
            return itemsFlow.map { items -> items.firstOrNull { it.id == id } }
        }

        override suspend fun getNextPage(): String? {
            return nextPage
        }

        override suspend fun replacePage(items: List<ArticleData>, nextPage: String?) {
            replacePageCallCount++
            itemsFlow.value = items
            this.nextPage = nextPage
        }

        override suspend fun appendPage(items: List<ArticleData>, nextPage: String?) {
            appendPageCallCount++
            val newIds = items.map { it.id }.toSet()
            itemsFlow.value = itemsFlow.value.filterNot { it.id in newIds } + items
            this.nextPage = nextPage
        }

        override suspend fun insertItems(items: List<ArticleData>) {
            error("Not used")
        }

        override suspend fun deleteItemById(id: Long) {
            error("Not used")
        }

        override suspend fun clearItems() {
            error("Not used")
        }

        override suspend fun upsertPagingState(pagingState: ArticlePagingStateData) {
            error("Not used")
        }
    }
}

private const val API = "https://api.test/v4"
private const val BASE_URL = "$API/"
