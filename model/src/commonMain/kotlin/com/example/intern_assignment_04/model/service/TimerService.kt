package com.example.intern_assignment_04.model.service

interface TimerService {
    fun start(durationMillis: Long)

    fun pause()

    fun reset()
}
