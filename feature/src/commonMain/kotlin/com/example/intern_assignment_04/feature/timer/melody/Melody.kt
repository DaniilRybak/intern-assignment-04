package com.example.intern_assignment_04.feature.timer.melody

/** Доменная модель мелодии для воспроизведения по завершению таймера. */
data class Melody(
    val id: Long,
    val title: String,
    val artist: String,
    val previewUrl: String,
)
