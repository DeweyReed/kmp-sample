package com.github.deweyreed.souvenir.feature.home.data

import com.github.deweyreed.souvenir.feature.home.api.ArticleEntity
import com.github.deweyreed.souvenir.feature.home.api.ArticleRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val articleRepositoryModule = module {
    factoryOf(::ArticleRepositoryImpl).bind<ArticleRepository>()
}

private class ArticleRepositoryImpl(private val httpClient: HttpClient) : ArticleRepository {
    override suspend fun getArticles(): Result<List<ArticleEntity>> {
        return runCatching {
            httpClient.get("https://api.spaceflightnewsapi.net/v4/articles/")
                .body<ArticleResult>()
                .results
                .map(ArticleResult.Result::toEntity)
        }
    }
}
