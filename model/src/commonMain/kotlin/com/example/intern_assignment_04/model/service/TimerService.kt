package com.example.intern_assignment_04.model.service

/** Контракт управления таймером: старт, пауза и сброс. */
interface TimerService {
    /** Запускает таймер на переданную длительность в миллисекундах. */
    fun start(durationMillis: Long)

    /** Приостанавливает текущий отсчет. */
    fun pause()

    /** Сбрасывает таймер в исходное состояние. */
    fun reset()
}
