package com.github.deweyreed.souvenir.feature.home.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ArticlePagingState")
data class ArticlePagingStateData(
    @PrimaryKey(autoGenerate = false) // Make sure the id is stable
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "next_page")
    val nextPage: String?,
)
