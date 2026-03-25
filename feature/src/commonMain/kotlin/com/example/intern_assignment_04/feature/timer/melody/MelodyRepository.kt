package com.example.intern_assignment_04.feature.timer.melody

interface MelodyRepository {
    suspend fun searchMelodies(
        query: String,
        limit: Int = 20,
    ): List<Melody>
}

