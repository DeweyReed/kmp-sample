package com.github.deweyreed.souvenir.feature.home.data

import com.github.deweyreed.souvenir.base.api.Qualifiers
import com.github.deweyreed.souvenir.base.api.Settings
import com.github.deweyreed.souvenir.feature.home.api.ArticleRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
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

class ArticleRepositoryImplTest : KoinTest {
    private val testDispatcher: CoroutineDispatcher = StandardTestDispatcher()

    private val repository: ArticleRepository by inject()
    private val dao: ArticleDao by inject()

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
                                content = "{}",
                                status = HttpStatusCode.OK,
                                headers = headersOf(
                                    HttpHeaders.ContentType, "application/json"
                                ),
                            )
                        }
                        HttpClient(mockEngine)
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
