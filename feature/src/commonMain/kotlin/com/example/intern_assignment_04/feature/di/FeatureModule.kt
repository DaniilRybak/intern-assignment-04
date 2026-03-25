package com.example.intern_assignment_04.feature.di

import com.example.intern_assignment_04.feature.stopwatch.StopwatchViewModel
import com.example.intern_assignment_04.feature.timer.TimerViewModel
import com.example.intern_assignment_04.feature.timer.melody.ITunesMelodyRepository
import com.example.intern_assignment_04.feature.timer.melody.MelodyRepository
import com.example.intern_assignment_04.feature.usecase.FormatTimeUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlin.time.Clock
import org.koin.dsl.module

internal typealias NowMillisProvider = () -> Long

internal val featureModule = module {

    single<CoroutineDispatcher> {
        Dispatchers.Default
    }

    single<NowMillisProvider> {
        { Clock.System.now().toEpochMilliseconds() }
    }

    single<MelodyRepository> {
        ITunesMelodyRepository()
    }

    factory {
        FormatTimeUseCase()
    }

    factory {
        TimerViewModel(
            dispatcher = get(),
            nowMillis = get(),
            melodyRepository = get(),
        )
    }
    factory {
        StopwatchViewModel(
            formatTimeUseCase = get(),
            dispatcher = get(),
            nowMillis = get(),
        )
    }
}
