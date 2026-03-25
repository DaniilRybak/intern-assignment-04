package com.example.intern_assignment_04.model.domain

/** Набор состояний секундомера прямого отсчета. */
sealed class StopwatchState {
    abstract val elapsedTimeMillis: Long
    abstract val laps: List<LapTime>

    /** Секундомер в исходном состоянии, без активного отсчета. */
    data class Idle(
        override val elapsedTimeMillis: Long = 0L,
        override val laps: List<LapTime> = emptyList(),
    ) : StopwatchState()

    /** Секундомер запущен и обновляет прошедшее время. */
    data class Running(
        override val elapsedTimeMillis: Long,
        override val laps: List<LapTime>,
    ) : StopwatchState()

    /** Секундомер поставлен на паузу и хранит накопленное время. */
    data class Paused(
        override val elapsedTimeMillis: Long,
        override val laps: List<LapTime>,
    ) : StopwatchState()
}
