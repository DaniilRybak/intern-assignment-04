package com.example.intern_assignment_04.feature.timer.melody

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

internal actual fun createItunesHttpClient(): HttpClient {
    val jsonConfig = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    return HttpClient(Android) {
        install(ContentNegotiation) {
            json(jsonConfig)
            json(jsonConfig, contentType = ContentType.Text.JavaScript)
        }
    }
}
