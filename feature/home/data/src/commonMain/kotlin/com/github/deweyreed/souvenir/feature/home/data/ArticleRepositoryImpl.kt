package com.github.deweyreed.souvenir.feature.home.data

import com.github.deweyreed.souvenir.feature.home.api.ArticleEntity
import com.github.deweyreed.souvenir.feature.home.api.ArticleRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private val httpClient by lazy {
    HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        }
    }
}

class ArticleRepositoryImpl : ArticleRepository {
    override suspend fun getArticles(): Result<List<ArticleEntity>> {
        return runCatching {
            httpClient.get("https://api.spaceflightnewsapi.net/v4/articles/")
                .body<ArticleResult>()
                .results
                .map(ArticleResult.Result::toEntity)
        }
    }
}
