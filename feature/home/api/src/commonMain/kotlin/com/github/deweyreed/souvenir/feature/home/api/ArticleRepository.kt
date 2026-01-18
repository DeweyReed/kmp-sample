package com.github.deweyreed.souvenir.feature.home.api

import kotlinx.coroutines.flow.Flow

interface ArticleRepository {
    fun getItemsFlow(): Flow<List<ArticleEntity>>
    fun getItemFlow(id: Long): Flow<ArticleEntity?>
}
