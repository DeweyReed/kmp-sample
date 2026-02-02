package com.github.deweyreed.souvenir.feature.home.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.deweyreed.souvenir.feature.home.api.ArticleEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity(tableName = "Article")
data class ArticleData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "database_id")
    val databaseId: Long = 0L,
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "url")
    val url: String,
    @ColumnInfo(name = "image_url")
    val imageUrl: String,
    @ColumnInfo(name = "summary")
    val summary: String,
    @ColumnInfo(name = "published_at")
    val publishedAt: String,
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
internal data class ArticleRemoteData(
    @SerialName("count")
    val count: Int,
    @SerialName("next")
    val next: String? = null,
    @SerialName("previous")
    val previous: String? = null,
    @SerialName("results")
    val results: List<Result> = emptyList(),
) {
    @Serializable
    internal data class Result(
        @SerialName("id")
        val id: Long,
        @SerialName("title")
        val title: String,
        @SerialName("authors")
        val authors: List<Author>,
        @SerialName("url")
        val url: String,
        @SerialName("image_url")
        val imageUrl: String,
        @SerialName("news_site")
        val newsSite: String,
        @SerialName("summary")
        val summary: String,
        @SerialName("published_at")
        val publishedAt: String,
        @SerialName("updated_at")
        val updatedAt: String,
        @SerialName("featured")
        val featured: Boolean,
    ) {
        @Serializable
        internal data class Author(
            @SerialName("name")
            val name: String,
            @SerialName("socials")
            val socials: Socials? = null,
        ) {
            @Serializable
            internal data class Socials(
                @SerialName("x")
                val x: String? = null,
                @SerialName("youtube")
                val youtube: String? = null,
                @SerialName("instagram")
                val instagram: String? = null,
                @SerialName("linkedin")
                val linkedin: String? = null,
                @SerialName("mastodon")
                val mastodon: String? = null,
                @SerialName("bluesky")
                val bluesky: String? = null,
            )
        }

        fun toData(): ArticleData {
            return ArticleData(
                id = id,
                title = title,
                url = url,
                imageUrl = imageUrl,
                summary = summary,
                publishedAt = publishedAt,
                updatedAt = updatedAt,
            )
        }
    }
}
