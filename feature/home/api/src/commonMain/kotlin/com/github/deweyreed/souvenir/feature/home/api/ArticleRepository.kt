package com.github.deweyreed.souvenir.feature.home.api

interface ArticleRepository {
    suspend fun getArticles(): Result<List<ArticleEntity>>
}
