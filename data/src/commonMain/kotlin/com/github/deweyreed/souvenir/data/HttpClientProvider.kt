package com.github.deweyreed.souvenir.data

import com.github.deweyreed.souvenir.base.api.Constants
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

@ContributesTo(AppScope::class)
interface HttpClientProvider {
    @SingleIn(AppScope::class)
    @Provides
    fun getHttpClient(): HttpClient {
        return HttpClient {
            defaultRequest {
                url(Constants.API)
            }
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
        }
    }
}
