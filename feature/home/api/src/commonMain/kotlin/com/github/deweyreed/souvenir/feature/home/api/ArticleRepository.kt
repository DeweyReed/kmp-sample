package com.github.deweyreed.souvenir.feature.home.api

import com.github.deweyreed.souvenir.base.api.Pagination
import kotlinx.coroutines.flow.Flow

interface ArticleRepository {
    fun getItemsPagination(): Pagination<ArticleEntity>
    fun getItemFlow(id: Long): Flow<ArticleEntity?>
    suspend fun clearItems()
}
