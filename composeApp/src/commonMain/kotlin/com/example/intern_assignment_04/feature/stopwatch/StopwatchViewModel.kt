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
import kotlinx.coroutines.flow.update
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

    private companion object {
        private const val TICK_INTERVAL_MILLIS = 16L
    }

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
                delay(TICK_INTERVAL_MILLIS)
                mutableState.update { currentState ->
                    when (currentState) {
                        is StopwatchState.Running -> currentState.copy(
                            elapsedTimeMillis = currentElapsedMillis(currentState),
                        )

                        is StopwatchState.Idle -> currentState
                        is StopwatchState.Paused -> currentState
                    }
                }
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
        mutableState.update { currentState ->
            if (currentState !is StopwatchState.Running) {
                return@update currentState
            }

            val totalElapsedMillis = currentElapsedMillis(currentState)
            val previousLapTotalMillis = currentState.laps.lastOrNull()?.totalElapsedMillis ?: 0L
            val nextLap = LapTime(
                lapNumber = currentState.laps.size + 1,
                lapDurationMillis = (totalElapsedMillis - previousLapTotalMillis).coerceAtLeast(0L),
                totalElapsedMillis = totalElapsedMillis,
            )

            currentState.copy(
                elapsedTimeMillis = totalElapsedMillis,
                laps = currentState.laps + nextLap,
            )
        }
    }

    fun formatElapsedTime(elapsedMillis: Long): String = formatTimeUseCase(elapsedMillis)

    fun formatElapsedTime(): String = formatElapsedTime(currentElapsedMillis())

    fun clear() {
        scope.cancel()
    }

    private fun currentElapsedMillis(
        state: StopwatchState = mutableState.value,
    ): Long {
        return when (state) {
            is StopwatchState.Idle -> state.elapsedTimeMillis
            is StopwatchState.Paused -> state.elapsedTimeMillis
            is StopwatchState.Running -> elapsedAtStartMillis + (nowMillis() - startedAtMillis)
        }
    }
}
