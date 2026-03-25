package com.example.intern_assignment_04.feature.timer.melody

/** Контракт поиска мелодий для выбора сигнала таймера. */
interface MelodyRepository {
    /** Выполняет поиск мелодий по запросу и возвращает ограниченный список результатов. */
    suspend fun searchMelodies(
        query: String,
        limit: Int = 20,
    ): List<Melody>
}
