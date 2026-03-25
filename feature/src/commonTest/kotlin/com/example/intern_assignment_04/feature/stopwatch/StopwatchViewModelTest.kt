package com.example.intern_assignment_04.feature.stopwatch

import com.example.intern_assignment_04.feature.usecase.FormatTimeUseCase
import com.example.intern_assignment_04.model.domain.StopwatchState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/** Набор тестов поведения [StopwatchViewModel]. */
@OptIn(ExperimentalCoroutinesApi::class)
class StopwatchViewModelTest {

    /** Проверяет, что секундомер корректно сохраняет длительность каждого круга. */
    @Test
    fun shouldRecordLapTimesInStopwatch() = runTest {
        var now = 0L
        val dispatcher = StandardTestDispatcher(testScheduler)
        val viewModel = StopwatchViewModel(
            formatTimeUseCase = FormatTimeUseCase(),
            dispatcher = dispatcher,
            nowMillis = { now },
        )

        viewModel.start()

        now = 1_500L
        viewModel.recordLap()

        now = 2_400L
        viewModel.recordLap()

        val state = assertIs<StopwatchState.Running>(viewModel.state.value)
        assertEquals(2, state.laps.size)
        assertEquals(1_500L, state.laps[0].lapDurationMillis)
        assertEquals(900L, state.laps[1].lapDurationMillis)

        viewModel.clear()
    }
}
