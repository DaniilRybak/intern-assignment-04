package com.example.intern_assignment_04.feature.timer.melody

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** DTO-обертка ответа iTunes Search API. */
@Serializable
internal data class ITunesSearchResponseDto(
    @SerialName("results")
    val results: List<ITunesTrackDto> = emptyList(),
)

/** DTO записи трека из выдачи iTunes. */
@Serializable
internal data class ITunesTrackDto(
    @SerialName("trackId")
    val trackId: Long? = null,
    @SerialName("trackName")
    val trackName: String? = null,
    @SerialName("artistName")
    val artistName: String? = null,
    @SerialName("previewUrl")
    val previewUrl: String? = null,
)
