package com.example.intern_assignment_04.model.domain

/**
 * Описывает один круг секундомера:
 * порядковый номер, длительность круга и общее время на момент фиксации.
 */
data class LapTime(
    val lapNumber: Int,
    val lapDurationMillis: Long,
    val totalElapsedMillis: Long,
)
