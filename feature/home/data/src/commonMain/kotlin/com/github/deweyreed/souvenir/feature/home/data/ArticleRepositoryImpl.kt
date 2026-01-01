package com.github.deweyreed.souvenir.feature.home.data

import com.github.deweyreed.souvenir.feature.home.api.ArticleEntity
import com.github.deweyreed.souvenir.feature.home.api.ArticleRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

@Inject
@ContributesBinding(AppScope::class)
class ArticleRepositoryImpl(private val httpClient: HttpClient) : ArticleRepository {
    override suspend fun getArticles(): Result<List<ArticleEntity>> {
        return runCatching {
            httpClient.get("https://api.spaceflightnewsapi.net/v4/articles/")
                .body<ArticleResult>()
                .results
                .map(ArticleResult.Result::toEntity)
        }
    }
}
