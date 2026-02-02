package com.github.deweyreed.souvenir.feature.home.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {
    @Query("SELECT * FROM Article ORDER BY database_id")
    fun getItemsFlow(): Flow<List<ArticleData>>

    @Query("SELECT * FROM Article WHERE id = :id LIMIT 1")
    fun getItemFlow(id: Long): Flow<ArticleData?>

    @Upsert
    suspend fun insertItems(items: List<ArticleData>)

    @Query("DELETE FROM Article")
    suspend fun clearItems()
}
