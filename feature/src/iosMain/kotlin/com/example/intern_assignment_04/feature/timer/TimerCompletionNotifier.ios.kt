package com.example.intern_assignment_04.feature.timer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSUUID
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter

@Composable
internal actual fun rememberTimerCompletionNotifier(): TimerCompletionNotifier {
    return remember {
        IOSTimerCompletionNotifier()
    }
}

private class IOSTimerCompletionNotifier : TimerCompletionNotifier {

    private val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()

    init {
        notificationCenter.requestAuthorizationWithOptions(
            options = UNAuthorizationOptionAlert or UNAuthorizationOptionSound,
            completionHandler = { _, _ -> },
        )
    }

    override fun notifyTimerCompleted(title: String, body: String) {
        val content = UNMutableNotificationContent().apply {
            setTitle(title)
            setBody(body)
        }

        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
            timeInterval = 0.1,
            repeats = false,
        )

        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = NSUUID().UUIDString(),
            content = content,
            trigger = trigger,
        )

        notificationCenter.addNotificationRequest(
            request = request,
            withCompletionHandler = null,
        )
    }
}

