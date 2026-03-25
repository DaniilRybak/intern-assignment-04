package com.example.intern_assignment_04.feature.timer.melody

import io.ktor.client.HttpClient

/** Создает платформенный Ktor-клиент для работы с iTunes API. */
internal expect fun createItunesHttpClient(): HttpClient
