package com.example.intern_assignment_04

/** Общий контракт для предоставления информации о текущей платформе. */
interface Platform {
    val name: String
}

/** Возвращает платформенную реализацию [Platform]. */
expect fun getPlatform(): Platform
