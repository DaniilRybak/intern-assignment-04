package com.example.intern_assignment_04.model.domain

sealed class TimerState {
    abstract val totalTimeMillis: Long
    abstract val remainingTimeMillis: Long

    data class Idle(
        override val totalTimeMillis: Long = 0L,
        override val remainingTimeMillis: Long = 0L,
    ) : TimerState()

    data class Running(
        override val totalTimeMillis: Long,
        override val remainingTimeMillis: Long,
    ) : TimerState()

    data class Paused(
        override val totalTimeMillis: Long,
        override val remainingTimeMillis: Long,
    ) : TimerState()

    data class Finished(
        override val totalTimeMillis: Long,
        override val remainingTimeMillis: Long = 0L,
    ) : TimerState()
}
