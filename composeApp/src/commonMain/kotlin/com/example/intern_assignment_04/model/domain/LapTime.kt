package com.example.intern_assignment_04.model.domain

internal data class LapTime(
    val lapNumber: Int,
    val lapDurationMillis: Long,
    val totalElapsedMillis: Long,
)

