package com.github.deweyreed.souvenir.feature.home.data

import com.github.deweyreed.souvenir.feature.home.api.ArticleEntity
import com.github.deweyreed.souvenir.feature.home.api.ArticleRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val articleRepositoryModule = module {
    factoryOf(::ArticleRepositoryImpl).bind<ArticleRepository>()
}

private class ArticleRepositoryImpl(
    private val dao: ArticleDao,
    private val httpClient: HttpClient,
) : ArticleRepository {
    override fun getArticlesFlow(): Flow<List<ArticleEntity>> {
        return dao.getItemsFlow()
            .onStart {
                runCatching {
                    dao.clearItems()
                    val results =
                        httpClient.get("https://api.spaceflightnewsapi.net/v4/articles/")
                            .body<ArticleResult>()
                            .results
                    dao.insertItems(results)
                }
            }
            .map { list ->
                list.map(ArticleData::toEntity)
            }
    }
}
