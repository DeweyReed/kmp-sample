package com.github.deweyreed.souvenir.feature.home.data

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {
    @Query("SELECT * FROM Article ORDER BY database_id")
    fun getItemsFlow(): Flow<List<ArticleData>>

    @Query("SELECT COUNT(*) FROM Article")
    suspend fun getItemCount(): Int

    @Query("SELECT * FROM Article WHERE id = :id LIMIT 1")
    fun getItemFlow(id: Long): Flow<ArticleData?>

    @Query("SELECT next_page FROM ArticlePagingState WHERE id = 0")
    suspend fun getNextPage(): String?

    @Upsert
    suspend fun insertItems(items: List<ArticleData>)

    @Query("DELETE FROM Article WHERE id = :id")
    suspend fun deleteItemById(id: Long)

    @Query("DELETE FROM Article")
    suspend fun clearItems()

    @Upsert
    suspend fun upsertPagingState(pagingState: ArticlePagingStateData)

    @Transaction
    suspend fun replacePage(items: List<ArticleData>, nextPage: String?) {
        clearItems()
        insertItems(items)
        upsertPagingState(ArticlePagingStateData(nextPage = nextPage))
    }

    @Transaction
    suspend fun appendPage(items: List<ArticleData>, nextPage: String?) {
        items.forEach { deleteItemById(it.id) }
        insertItems(items)
        upsertPagingState(ArticlePagingStateData(nextPage = nextPage))
    }
}
