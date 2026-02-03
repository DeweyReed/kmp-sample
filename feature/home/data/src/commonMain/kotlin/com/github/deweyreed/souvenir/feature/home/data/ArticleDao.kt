package com.github.deweyreed.souvenir.feature.home.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {
    @Query("SELECT * FROM Article ORDER BY database_id")
    fun getItemsFlow(): Flow<List<ArticleData>>

    @Query("SELECT COUNT(*) FROM Article")
    suspend fun getItemCount(): Int

    @Query("SELECT * FROM Article WHERE id = :id LIMIT 1")
    fun getItemFlow(id: Long): Flow<ArticleData?>

    @Upsert
    suspend fun insertItems(items: List<ArticleData>)

    @Query("DELETE FROM Article WHERE id = :id")
    suspend fun deleteItemById(id: Long)

    @Query("DELETE FROM Article")
    suspend fun clearItems()

    @Transaction
    suspend fun insertItemsWithoutDuplicates(items: List<ArticleData>) {
        items.forEach { deleteItemById(it.id) }
        insertItems(items)
    }
}
