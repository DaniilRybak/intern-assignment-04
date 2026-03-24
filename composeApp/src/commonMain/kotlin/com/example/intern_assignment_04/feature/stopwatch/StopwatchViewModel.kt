package com.example.intern_assignment_04.feature.stopwatch

import com.example.intern_assignment_04.feature.usecase.FormatTimeUseCase
import com.example.intern_assignment_04.model.domain.LapTime
import com.example.intern_assignment_04.model.domain.StopwatchState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

internal class StopwatchViewModel(
    private val formatTimeUseCase: FormatTimeUseCase,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    private val nowMillis: () -> Long,
) {

    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private val mutableState = MutableStateFlow<StopwatchState>(StopwatchState.Idle())

    val state: StateFlow<StopwatchState> = mutableState.asStateFlow()

    private var tickerJob: Job? = null
    private var startedAtMillis: Long = 0L
    private var elapsedAtStartMillis: Long = 0L

    fun start() {
        if (tickerJob?.isActive == true) {
            return
        }

        val elapsedMillis = currentElapsedMillis()
        val laps = mutableState.value.laps

        startedAtMillis = nowMillis()
        elapsedAtStartMillis = elapsedMillis
        mutableState.value = StopwatchState.Running(
            elapsedTimeMillis = elapsedMillis,
            laps = laps,
        )

        tickerJob = scope.launch {
            while (isActive) {
                delay(1000L)
                mutableState.value = StopwatchState.Running(
                    elapsedTimeMillis = currentElapsedMillis(),
                    laps = mutableState.value.laps,
                )
            }
        }
    }

    fun stop() {
        val elapsedMillis = currentElapsedMillis()
        val laps = mutableState.value.laps

        tickerJob?.cancel()
        tickerJob = null
        mutableState.value = StopwatchState.Paused(
            elapsedTimeMillis = elapsedMillis,
            laps = laps,
        )
    }

    fun reset() {
        tickerJob?.cancel()
        tickerJob = null
        startedAtMillis = 0L
        elapsedAtStartMillis = 0L

        mutableState.value = StopwatchState.Idle(
            elapsedTimeMillis = 0L,
            laps = emptyList(),
        )
    }

    fun recordLap() {
        if (mutableState.value !is StopwatchState.Running) {
            return
        }

        val totalElapsedMillis = currentElapsedMillis()
        val currentLaps = mutableState.value.laps
        val previousLapTotalMillis = currentLaps.lastOrNull()?.totalElapsedMillis ?: 0L
        val nextLap = LapTime(
            lapNumber = currentLaps.size + 1,
            lapDurationMillis = (totalElapsedMillis - previousLapTotalMillis).coerceAtLeast(0L),
            totalElapsedMillis = totalElapsedMillis,
        )

        mutableState.value = StopwatchState.Running(
            elapsedTimeMillis = totalElapsedMillis,
            laps = currentLaps + nextLap,
        )
    }

    fun formatElapsedTime(): String = formatTimeUseCase(currentElapsedMillis())

    fun clear() {
        scope.cancel()
    }

    private fun currentElapsedMillis(): Long {
        return when (val currentState = mutableState.value) {
            is StopwatchState.Idle -> currentState.elapsedTimeMillis
            is StopwatchState.Paused -> currentState.elapsedTimeMillis
            is StopwatchState.Running -> elapsedAtStartMillis + (nowMillis() - startedAtMillis)
        }
    }
}
