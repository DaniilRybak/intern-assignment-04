package com.example.intern_assignment_04

import android.os.Build

/** Android-реализация, возвращающая имя ОС и уровень API. */
class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

/** Возвращает Android-реализацию интерфейса [Platform]. */
actual fun getPlatform(): Platform = AndroidPlatform()
