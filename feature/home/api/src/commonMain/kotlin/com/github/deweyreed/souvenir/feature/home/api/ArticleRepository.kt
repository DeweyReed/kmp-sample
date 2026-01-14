package com.github.deweyreed.souvenir.feature.home.api

import kotlinx.coroutines.flow.Flow

interface ArticleRepository {
    fun getArticlesFlow(): Flow<List<ArticleEntity>>
}
