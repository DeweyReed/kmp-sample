package com.github.deweyreed.souvenir.feature.home.data

import com.github.deweyreed.souvenir.feature.home.api.ArticleRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ArticleRepositoryTest {
    private val testDispatcher = StandardTestDispatcher()

    private val dao: ArticleDao = FakeArticleDao()
    private val repository: ArticleRepository = ArticleRepositoryImpl(
        ioDispatcher = testDispatcher,
        settings = FakeSettings(),
        dao = dao,
        httpClient = HttpClient(
            MockEngine { _ ->
                respond(
                    content = ByteReadChannel(mockResponse.value),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }
        ) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
        },
    )

    private val mockResponse = MutableStateFlow("{}")

    @Test
    fun `getItemsPagination should load more on start when empty`() = runTest(testDispatcher) {
        mockResponse.value = """
            {
                "count": 1,
                "results": [
                    {
                        "id": 1,
                        "title": "T",
                        "authors": [],
                        "url": "U",
                        "image_url": "I",
                        "news_site": "N",
                        "summary": "S",
                        "published_at": "P",
                        "updated_at": "U",
                        "featured": false
                    }
                ]
            }
        """.trimIndent()

        val pagination = repository.getItemsPagination()
        val items = pagination.flow.first { it.isNotEmpty() }

        assertEquals(1, items.size)
        assertEquals(1L, items[0].id)
        assertEquals(1, dao.getItemCount())
    }

    @Test
    fun `getItemsPagination shouldn't load more on start when not empty`() =
        runTest(testDispatcher) {
            mockResponse.value = """
            {
                "count": 1,
                "results": [
                    {
                        "id": 2,
                        "title": "T",
                        "authors": [],
                        "url": "U",
                        "image_url": "I",
                        "news_site": "N",
                        "summary": "S",
                        "published_at": "P",
                        "updated_at": "U",
                        "featured": false
                    }
                ]
            }
        """.trimIndent()

            dao.insertItems(
                listOf(
                    ArticleData(
                        id = 1,
                        title = "T",
                        url = "U",
                        imageUrl = "I",
                        summary = "S",
                        publishedAt = "P",
                        updatedAt = "U",
                    ),
                )
            )

            val pagination = repository.getItemsPagination()
            val items = pagination.flow.first()

            assertEquals(1, items.size)
            assertEquals(1, dao.getItemCount())
        }

    @Test
    fun `getItemsPagination loadMore should fetch and save items`() = runTest(testDispatcher) {
        mockResponse.value = """
            {
                "count": 1,
                "next": "next_url",
                "results": [
                    {
                        "id": 1,
                        "title": "T",
                        "authors": [],
                        "url": "U",
                        "image_url": "I",
                        "news_site": "N",
                        "summary": "S",
                        "published_at": "P",
                        "updated_at": "U",
                        "featured": false
                    }
                ]
            }
        """.trimIndent()

        val pagination = repository.getItemsPagination()
        pagination.loadMore()

        assertEquals(1, dao.getItemCount())
    }

    @Test
    fun `getItemFlow should return item`() = runTest(testDispatcher) {
        val article = ArticleData(
            id = 1,
            title = "T",
            url = "U",
            imageUrl = "I",
            summary = "S",
            publishedAt = "P",
            updatedAt = "U",
        )
        dao.insertItems(listOf(article))
        val result = repository.getItemFlow(1).first()
        assertEquals(article.id, result?.id)
        assertEquals(article.title, result?.title)
    }

    @Test
    fun `getItemFlow should return null when not found`() = runTest(testDispatcher) {
        val result = repository.getItemFlow(1).first()
        assertNull(result)
    }

    @Test
    fun `clearItems should reset settings and dao`() = runTest(testDispatcher) {
        dao.insertItems(
            listOf(
                ArticleData(
                    id = 1,
                    title = "T",
                    url = "U",
                    imageUrl = "I",
                    summary = "S",
                    publishedAt = "P",
                    updatedAt = "U",
                ),
            )
        )
        repository.clearItems()
        assertEquals(0, dao.getItemCount())
    }
}
