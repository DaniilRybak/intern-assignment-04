package com.example.intern_assignment_04.feature.timer

import androidx.compose.runtime.Composable

/** Платформенный контракт уведомления пользователя о завершении таймера. */
internal interface TimerCompletionNotifier {
    /** Показывает системное уведомление о завершении таймера. */
    fun notifyTimerCompleted(title: String, body: String)

    /** Запускает предпрослушивание выбранной мелодии. */
    fun playMelodyPreview(previewUrl: String)

    /** Останавливает текущее воспроизведение мелодии. */
    fun stopPlayback()
}

/** Возвращает платформенную реализацию [TimerCompletionNotifier]. */
@Composable
internal expect fun rememberTimerCompletionNotifier(): TimerCompletionNotifier
