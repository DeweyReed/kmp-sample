package com.github.deweyreed.souvenir.data

import com.github.deweyreed.souvenir.base.api.Constants
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val networkModule: Module = module {
    singleOf(::getHttpClient)
}

private fun getHttpClient(): HttpClient {
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
