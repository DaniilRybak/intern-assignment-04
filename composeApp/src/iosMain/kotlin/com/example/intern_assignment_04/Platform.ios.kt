package com.example.intern_assignment_04

import platform.UIKit.UIDevice

/** iOS-реализация, возвращающая имя системы и ее версию. */
class IOSPlatform : Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

/** Возвращает iOS-реализацию интерфейса [Platform]. */
actual fun getPlatform(): Platform = IOSPlatform()
