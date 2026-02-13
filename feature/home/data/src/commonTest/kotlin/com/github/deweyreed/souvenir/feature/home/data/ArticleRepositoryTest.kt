package com.github.deweyreed.souvenir.feature.home.data

import com.github.deweyreed.souvenir.base.api.Qualifiers
import com.github.deweyreed.souvenir.base.api.Settings
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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ArticleRepositoryTest : KoinTest {
    private val testDispatcher: CoroutineDispatcher = StandardTestDispatcher()

    private val repository: ArticleRepository by inject()
    private val dao: ArticleDao by inject()

    private val mockResponse = MutableStateFlow("{}")

    @BeforeTest
    fun setUp() {
        startKoin {
            modules(
                module {
                    single<CoroutineDispatcher>(named<Qualifiers.Dispatchers.Io>()) {
                        testDispatcher
                    }
                    single<Settings> { FakeSettings() }
                    single<suspend (suspend () -> Unit) -> Unit>(
                        named<Qualifiers.DatabaseTransaction>()
                    ) {
                        val transaction: suspend (suspend () -> Unit) -> Unit = { it() }
                        transaction
                    }
                    single<ArticleDao> { FakeArticleDao() }
                    single<HttpClient> {
                        val mockEngine = MockEngine { _ ->
                            respond(
                                content = ByteReadChannel(mockResponse.value),
                                status = HttpStatusCode.OK,
                                headers = headersOf(
                                    HttpHeaders.ContentType, "application/json",
                                ),
                            )
                        }
                        HttpClient(mockEngine) {
                            install(ContentNegotiation) {
                                json(
                                    Json {
                                        ignoreUnknownKeys = true
                                    }
                                )
                            }
                        }
                    }
                },
                articleRepositoryModule,
            )
        }
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun getItemsPagination_loadsMoreOnStart_whenEmpty() = runTest(testDispatcher) {
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
    fun getItemsPagination_doesNotLoadMoreOnStart_whenNotEmpty() = runTest(testDispatcher) {
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
    fun getItemsPagination_loadMore_fetchesAndSavesItems() = runTest(testDispatcher) {
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
    fun getItemFlow_returnsItem() = runTest(testDispatcher) {
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
    fun getItemFlow_returnsNull_whenNotFound() = runTest(testDispatcher) {
        val result = repository.getItemFlow(1).first()
        assertNull(result)
    }

    @Test
    fun clearItems_resetsSettingsAndDao() = runTest(testDispatcher) {
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
