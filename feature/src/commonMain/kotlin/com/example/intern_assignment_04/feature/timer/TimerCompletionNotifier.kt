package com.example.intern_assignment_04.feature.timer

import androidx.compose.runtime.Composable

internal interface TimerCompletionNotifier {
    fun notifyTimerCompleted(title: String, body: String)
    fun playMelodyPreview(previewUrl: String)
    fun stopPlayback()
}

@Composable
internal expect fun rememberTimerCompletionNotifier(): TimerCompletionNotifier
