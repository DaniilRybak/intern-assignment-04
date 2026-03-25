package com.example.intern_assignment_04.feature.timer.melody

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.HttpHeaders

/** Реализация [MelodyRepository], загружающая треки из iTunes Search API. */
class ITunesMelodyRepository(
    private val client: HttpClient = createItunesHttpClient(),
) : MelodyRepository {

    /** Запрашивает треки у iTunes и маппит корректные записи в доменную модель [Melody]. */
    override suspend fun searchMelodies(
        query: String,
        limit: Int,
    ): List<Melody> {
        val response = client.get("https://itunes.apple.com/search") {
            parameter("term", query)
            parameter("media", "music")
            parameter("entity", "song")
            parameter("limit", limit)
            header(HttpHeaders.Accept, "application/json, text/javascript")
        }.body<ITunesSearchResponseDto>()

        return response.results.mapNotNull { track ->
            val id = track.trackId ?: return@mapNotNull null
            val title = track.trackName ?: return@mapNotNull null
            val artist = track.artistName ?: return@mapNotNull null
            val previewUrl = track.previewUrl ?: return@mapNotNull null

            Melody(
                id = id,
                title = title,
                artist = artist,
                previewUrl = previewUrl,
            )
        }
    }
}
