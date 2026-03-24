package com.example.intern_assignment_04.model.domain

internal sealed class StopwatchState {
    abstract val elapsedTimeMillis: Long
    abstract val laps: List<LapTime>

    data class Idle(
        override val elapsedTimeMillis: Long = 0L,
        override val laps: List<LapTime> = emptyList(),
    ) : StopwatchState()

    data class Running(
        override val elapsedTimeMillis: Long,
        override val laps: List<LapTime>,
    ) : StopwatchState()

    data class Paused(
        override val elapsedTimeMillis: Long,
        override val laps: List<LapTime>,
    ) : StopwatchState()
}

