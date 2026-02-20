package com.github.deweyreed.souvenir.feature.home.data

import kotlin.test.Test
import kotlin.test.assertEquals

class ArticleDataTest {
    @Test
    fun `toEntity should map correct fields`() {
        val data = ArticleData(
            id = 123L,
            title = "Test Title",
            url = "https://example.com",
            imageUrl = "https://example.com/image.png",
            summary = "Test Summary",
            publishedAt = "2023-10-27T10:00:00Z",
            updatedAt = "2023-10-27T11:00:00Z"
        )
        val entity = data.toEntity()
        assertEquals(data.id, entity.id)
        assertEquals(data.title, entity.title)
        assertEquals(data.imageUrl, entity.imageUrl)
        assertEquals(data.summary, entity.summary)
    }
}
