package com.example.intern_assignment_04.feature.usecase

class FormatTimeUseCase {
    operator fun invoke(millis: Long): String {
        val totalSeconds = (millis.coerceAtLeast(0L) / 1000L)
        val minutes = (totalSeconds / 60L).toString().padStart(2, '0')
        val seconds = (totalSeconds % 60L).toString().padStart(2, '0')

        return "$minutes:$seconds"
    }
}
