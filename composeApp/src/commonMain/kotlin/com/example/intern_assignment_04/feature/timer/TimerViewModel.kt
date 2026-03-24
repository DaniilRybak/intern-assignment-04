package com.example.intern_assignment_04.feature.timer

import com.example.intern_assignment_04.model.domain.TimerState
import com.example.intern_assignment_04.model.service.TimerService
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

internal class TimerViewModel(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    private val nowMillis: () -> Long,
) : TimerService {

    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private val mutableState = MutableStateFlow<TimerState>(TimerState.Idle())

    val state: StateFlow<TimerState> = mutableState.asStateFlow()

    private var tickerJob: Job? = null
    private var startedAtMillis: Long = 0L
    private var remainingAtStartMillis: Long = 0L
    private var configuredTotalMillis: Long = 0L

    override fun start(durationMillis: Long) {
        if (tickerJob?.isActive == true) {
            return
        }

        val targetDurationMillis = durationMillis.coerceAtLeast(0L)
        if (targetDurationMillis > 0L) {
            configuredTotalMillis = targetDurationMillis
            remainingAtStartMillis = targetDurationMillis
        } else {
            remainingAtStartMillis = currentRemainingMillis()
        }

        if (remainingAtStartMillis <= 0L) {
            mutableState.value = TimerState.Finished(
                totalTimeMillis = configuredTotalMillis,
                remainingTimeMillis = 0L,
            )
            return
        }

        startedAtMillis = nowMillis()
        mutableState.value = TimerState.Running(
            totalTimeMillis = configuredTotalMillis,
            remainingTimeMillis = remainingAtStartMillis,
        )

        tickerJob = scope.launch {
            while (isActive) {
                delay(1000L)
                val updatedRemainingMillis = currentRemainingMillis()

                if (updatedRemainingMillis <= 0L) {
                    mutableState.value = TimerState.Finished(
                        totalTimeMillis = configuredTotalMillis,
                        remainingTimeMillis = 0L,
                    )
                    cancel()
                    break
                }

                mutableState.value = TimerState.Running(
                    totalTimeMillis = configuredTotalMillis,
                    remainingTimeMillis = updatedRemainingMillis,
                )
            }
        }
    }

    override fun stop() {
        val remainingMillis = currentRemainingMillis()
        tickerJob?.cancel()
        tickerJob = null

        mutableState.value = TimerState.Paused(
            totalTimeMillis = configuredTotalMillis,
            remainingTimeMillis = remainingMillis,
        )
    }

    override fun reset() {
        tickerJob?.cancel()
        tickerJob = null

        mutableState.value = TimerState.Idle(
            totalTimeMillis = configuredTotalMillis,
            remainingTimeMillis = configuredTotalMillis,
        )
    }

    fun formatRemainingTime(): String {
        val millis = currentRemainingMillis().coerceAtLeast(0L)
        val totalSeconds = millis / 1000L
        val hours = (totalSeconds / 3600L).toString().padStart(2, '0')
        val minutes = ((totalSeconds % 3600L) / 60L).toString().padStart(2, '0')

        return "$hours:$minutes"
    }

    fun clear() {
        scope.cancel()
    }

    private fun currentRemainingMillis(): Long {
        return when (val currentState = mutableState.value) {
            is TimerState.Idle -> currentState.remainingTimeMillis
            is TimerState.Paused -> currentState.remainingTimeMillis
            is TimerState.Finished -> 0L
            is TimerState.Running -> {
                val elapsedMillis = nowMillis() - startedAtMillis
                (remainingAtStartMillis - elapsedMillis).coerceAtLeast(0L)
            }
        }
    }
}
