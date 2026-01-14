package com.github.deweyreed.souvenir.feature.home.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {
    @Query("SELECT * FROM Article ORDER BY published_at DESC")
    fun getItemsFlow(): Flow<List<ArticleData>>

    @Upsert
    suspend fun insertItems(items: List<ArticleData>)

    @Query("DELETE FROM Article")
    suspend fun clearItems()
}
