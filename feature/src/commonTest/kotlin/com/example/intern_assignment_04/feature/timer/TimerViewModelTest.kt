package com.example.intern_assignment_04.feature.timer

import com.example.intern_assignment_04.model.domain.TimerState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class TimerViewModelTest {

    @Test
    fun shouldCountdownFromSetTime() = runTest {
        var now = 0L
        val dispatcher = StandardTestDispatcher(testScheduler)
        val viewModel = TimerViewModel(
            dispatcher = dispatcher,
            nowMillis = { now },
        )

        try {
            viewModel.start(3_000L)
            val startedState = assertIs<TimerState.Running>(viewModel.state.value)
            assertEquals(3_000L, startedState.remainingTimeMillis)
            assertEquals("00:03", viewModel.formatRemainingTime())

            now = 1_000L
            tick(1_000L)

            val oneSecondState = assertIs<TimerState.Running>(viewModel.state.value)
            assertEquals(2_000L, oneSecondState.remainingTimeMillis)
            assertEquals("00:02", viewModel.formatRemainingTime())

            now = 3_000L
            tick(2_000L)

            val finishedState = assertIs<TimerState.Finished>(viewModel.state.value)
            assertEquals(0L, finishedState.remainingTimeMillis)
            assertEquals("00:00", viewModel.formatRemainingTime())
        } finally {
            viewModel.clear()
        }
    }

    @Test
    fun shouldPauseAndResumeTimer() = runTest {
        var now = 0L
        val dispatcher = StandardTestDispatcher(testScheduler)
        val viewModel = TimerViewModel(
            dispatcher = dispatcher,
            nowMillis = { now },
        )

        try {
            viewModel.start(5_000L)

            now = 2_000L
            tick(2_000L)

            viewModel.pause()
            val pausedState = assertIs<TimerState.Paused>(viewModel.state.value)
            assertEquals(3_000L, pausedState.remainingTimeMillis)

            // На паузе время не должно убывать, даже если часы идут вперед.
            now = 5_000L
            tick(3_000L)
            val stillPausedState = assertIs<TimerState.Paused>(viewModel.state.value)
            assertEquals(3_000L, stillPausedState.remainingTimeMillis)

            viewModel.start(0L)

            now = 6_000L
            tick(1_000L)

            val resumedState = assertIs<TimerState.Running>(viewModel.state.value)
            assertEquals(2_000L, resumedState.remainingTimeMillis)
        } finally {
            viewModel.clear()
        }
    }

    @Test
    fun shouldEmitFinishEventWhenTimerReachesZero() = runTest {
        var now = 0L
        val dispatcher = StandardTestDispatcher(testScheduler)
        val viewModel = TimerViewModel(
            dispatcher = dispatcher,
            nowMillis = { now },
        )

        try {
            viewModel.start(1_000L)

            now = 1_000L
            tick(1_000L)

            assertIs<TimerState.Finished>(viewModel.state.value)
        } finally {
            viewModel.clear()
        }
    }

    private fun TestScope.tick(millis: Long) {
        advanceTimeBy(millis)
        runCurrent()
    }
}
