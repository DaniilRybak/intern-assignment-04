package com.example.intern_assignment_04.feature.usecase

import kotlin.test.Test
import kotlin.test.assertEquals

class FormatTimeUseCaseTest {

    @Test
    fun shouldFormatTimeCorrectly() {
        val useCase = FormatTimeUseCase()

        assertEquals("00:00", useCase(-1L))
        assertEquals("00:00", useCase(0L))
        assertEquals("01:05", useCase(65_000L))
    }
}

