package com.github.deweyreed.souvenir.feature.home.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.deweyreed.souvenir.feature.home.api.ArticleEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "Article")
data class ArticleData(
    @SerialName("id")
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Long,
    @SerialName("title")
    @ColumnInfo(name = "title")
    val title: String,
    @SerialName("url")
    @ColumnInfo(name = "url")
    val url: String,
    @SerialName("image_url")
    @ColumnInfo(name = "image_url")
    val imageUrl: String,
    @SerialName("summary")
    @ColumnInfo(name = "summary")
    val summary: String,
    @SerialName("published_at")
    @ColumnInfo(name = "published_at")
    val publishedAt: String,
    @SerialName("updated_at")
    @ColumnInfo(name = "updated_at")
    val updatedAt: String,
)

internal fun ArticleData.toEntity(): ArticleEntity {
    return ArticleEntity(
        id = id,
        title = title,
        imageUrl = imageUrl,
        summary = summary,
    )
}

@Serializable
internal data class ArticleResult(
    @SerialName("count")
    val count: Int,
    @SerialName("next")
    val next: String? = null,
    @SerialName("previous")
    val previous: String? = null,
    @SerialName("results")
    val results: List<ArticleData> = emptyList(),
)
