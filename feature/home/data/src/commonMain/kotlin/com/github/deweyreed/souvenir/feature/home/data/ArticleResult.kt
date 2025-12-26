package com.github.deweyreed.souvenir.feature.home.data

import com.github.deweyreed.souvenir.feature.home.api.ArticleEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ArticleResult(
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
    data class Result(
        @SerialName("id")
        val id: Long,
        @SerialName("title")
        val title: String,
        @SerialName("url")
        val url: String,
        @SerialName("image_url")
        val imageUrl: String,
        @SerialName("summary")
        val summary: String,
        @SerialName("published_at")
        val publishedAt: String,
        @SerialName("updated_at")
        val updatedAt: String,
    ) {
        fun toEntity(): ArticleEntity {
            return ArticleEntity(
                id = id,
                title = title,
                imageUrl = imageUrl,
                summary = summary,
            )
        }
    }
}
