package com.github.deweyreed.souvenir.feature.home.data

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "ArticlePagingState")
data class ArticlePagingStateData(
    @PrimaryKey(autoGenerate = false) // Make sure the id is stable
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "next_page")
    val nextPage: String?,
)
