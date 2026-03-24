package com.example.intern_assignment_04

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform