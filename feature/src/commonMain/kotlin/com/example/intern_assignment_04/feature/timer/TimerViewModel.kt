package com.example.intern_assignment_04.feature.timer

import com.example.intern_assignment_04.feature.timer.melody.Melody
import com.example.intern_assignment_04.feature.timer.melody.MelodyRepository
import com.example.intern_assignment_04.model.domain.TimerState
import com.example.intern_assignment_04.model.service.TimerService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/** Управляет состоянием таймера, мелодиями и событиями завершения отсчета. */
class TimerViewModel(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    private val nowMillis: () -> Long,
    private val melodyRepository: MelodyRepository? = null,
) : TimerService {

    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private val mutableState = MutableStateFlow<TimerState>(TimerState.Idle())
    private val mutableMelodies = MutableStateFlow<List<Melody>>(emptyList())
    private val mutableSelectedMelody = MutableStateFlow<Melody?>(null)
    private val mutableMelodyLoading = MutableStateFlow(false)
    private val mutableMelodyError = MutableStateFlow<String?>(null)
    private val mutableEvents = MutableSharedFlow<TimerUiEvent>(extraBufferCapacity = 1)

    val state: StateFlow<TimerState> = mutableState.asStateFlow()
    val melodies: StateFlow<List<Melody>> = mutableMelodies.asStateFlow()
    val selectedMelody: StateFlow<Melody?> = mutableSelectedMelody.asStateFlow()
    val melodyLoading: StateFlow<Boolean> = mutableMelodyLoading.asStateFlow()
    val melodyError: StateFlow<String?> = mutableMelodyError.asStateFlow()
    internal val events: SharedFlow<TimerUiEvent> = mutableEvents.asSharedFlow()

    private var tickerJob: Job? = null
    private var startedAtMillis: Long = 0L
    private var remainingAtStartMillis: Long = 0L
    private var configuredTotalMillis: Long = 0L

    /** Запускает таймер на заданную длительность или продолжает после паузы. */
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
            finishTimer()
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
                    finishTimer()
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

    /** Ставит таймер на паузу и сохраняет остаток времени. */
    override fun pause() {
        val remainingMillis = currentRemainingMillis()
        tickerJob?.cancel()
        tickerJob = null

        mutableState.value = TimerState.Paused(
            totalTimeMillis = configuredTotalMillis,
            remainingTimeMillis = remainingMillis,
        )
    }

    /** Сбрасывает таймер в состояние Idle с последней выбранной длительностью. */
    override fun reset() {
        tickerJob?.cancel()
        tickerJob = null

        mutableState.value = TimerState.Idle(
            totalTimeMillis = configuredTotalMillis,
            remainingTimeMillis = configuredTotalMillis,
        )
    }

    /** Загружает список мелодий из репозитория для выбора сигнала. */
    fun loadMelodies(query: String = DEFAULT_ITUNES_QUERY) {
        if (mutableMelodyLoading.value) {
            return
        }

        val repository = melodyRepository ?: return

        scope.launch {
            mutableMelodyLoading.value = true
            mutableMelodyError.value = null

            try {
                val loaded = repository.searchMelodies(query = query)
                mutableMelodies.value = loaded

                if (mutableSelectedMelody.value == null) {
                    mutableSelectedMelody.value = loaded.firstOrNull()
                }
            } catch (throwable: Throwable) {
                mutableMelodyError.value = throwable.message ?: "Не удалось загрузить мелодии"
            } finally {
                mutableMelodyLoading.value = false
            }
        }
    }

    /** Выбирает мелодию по ее идентификатору. */
    fun selectMelody(melodyId: Long) {
        mutableSelectedMelody.value = mutableMelodies.value.firstOrNull { it.id == melodyId }
    }

    /** Форматирует оставшееся время в строку MM:SS. */
    fun formatRemainingTime(): String {
        val totalSeconds = currentRemainingMillis().coerceAtLeast(0L) / 1000L
        val minutes = totalSeconds / 60L
        val seconds = totalSeconds % 60L

        return "${minutes.twoDigits()}:${seconds.twoDigits()}"
    }

    /** Возвращает время для кругового индикатора в двух частях (часы:минуты и секунды). */
    internal fun formatRemainingTimeForCircle(): TimerCircleTime {
        val totalSeconds = currentRemainingMillis().coerceAtLeast(0L) / 1000L
        val hours = totalSeconds / 3600L
        val minutes = (totalSeconds % 3600L) / 60L
        val seconds = totalSeconds % 60L

        return TimerCircleTime(
            main = "${hours.twoDigits()}:${minutes.twoDigits()}",
            seconds = ",${seconds.twoDigits()}",
        )
    }

    /** Очищает scope view-модели и завершает связанные корутины. */
    fun clear() {
        scope.cancel()
    }

    /** Вычисляет актуальный остаток времени в зависимости от текущего состояния. */
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

    /** Переводит таймер в состояние завершения и отправляет событие UI. */
    private fun finishTimer() {
        mutableState.value = TimerState.Finished(
            totalTimeMillis = configuredTotalMillis,
            remainingTimeMillis = 0L,
        )
        mutableEvents.tryEmit(TimerUiEvent.TimerFinished)
    }

    private companion object {
        private const val DEFAULT_ITUNES_QUERY = "alarm"
    }
}

/** Возвращает строку числа с ведущим нулем до двух символов. */
private fun Long.twoDigits(): String = toString().padStart(2, '0')

/** Представление времени для кругового таймера: основная и вторичная части. */
internal data class TimerCircleTime(
    val main: String,
    val seconds: String,
)

/** События UI, которые эмитит таймер поверх состояния. */
internal sealed interface TimerUiEvent {
    /** Событие завершения обратного отсчета. */
    data object TimerFinished : TimerUiEvent
}
