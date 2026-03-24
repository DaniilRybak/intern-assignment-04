package com.example.intern_assignment_04.model.service

internal interface TimerService {
    fun start(durationMillis: Long)

    fun stop()

    fun reset()
}

