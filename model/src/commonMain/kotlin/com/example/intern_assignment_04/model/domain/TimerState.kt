package com.example.intern_assignment_04.model.domain

/** Набор состояний таймера обратного отсчета. */
sealed class TimerState {
    abstract val totalTimeMillis: Long
    abstract val remainingTimeMillis: Long

    /** Таймер не запущен, пользователь может выбрать длительность. */
    data class Idle(
        override val totalTimeMillis: Long = 0L,
        override val remainingTimeMillis: Long = 0L,
    ) : TimerState()

    /** Таймер активен и уменьшает оставшееся время каждую секунду. */
    data class Running(
        override val totalTimeMillis: Long,
        override val remainingTimeMillis: Long,
    ) : TimerState()

    /** Таймер остановлен пользователем и может быть продолжен. */
    data class Paused(
        override val totalTimeMillis: Long,
        override val remainingTimeMillis: Long,
    ) : TimerState()

    /** Таймер завершил обратный отсчет. */
    data class Finished(
        override val totalTimeMillis: Long,
        override val remainingTimeMillis: Long = 0L,
    ) : TimerState()
}
