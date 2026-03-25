package com.example.intern_assignment_04.feature.timer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.rate
import platform.Foundation.NSTimer
import platform.Foundation.NSURL
import platform.Foundation.NSUUID
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter

private const val MELODY_PREVIEW_MAX_DURATION_SECONDS = 10.0

@Composable
internal actual fun rememberTimerCompletionNotifier(): TimerCompletionNotifier {
    return remember {
        IOSTimerCompletionNotifier()
    }
}

private class IOSTimerCompletionNotifier : TimerCompletionNotifier {

    private val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()
    private var player: AVPlayer? = null
    private var stopPlaybackTimer: NSTimer? = null

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
            setSound(UNNotificationSound.defaultSound)
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

    override fun playMelodyPreview(previewUrl: String) {
        runCatching {
            clearPlayer()
            val url = NSURL.URLWithString(previewUrl) ?: return@runCatching
            player = AVPlayer.playerWithURL(url)
            player?.rate = 1.0f
            stopPlaybackTimer = NSTimer.scheduledTimerWithTimeInterval(
                MELODY_PREVIEW_MAX_DURATION_SECONDS,
                false,
            ) { _ ->
                clearPlayer()
            }
        }
    }

    private fun clearPlayer() {
        stopPlaybackTimer?.invalidate()
        stopPlaybackTimer = null
        player?.rate = 0.0f
        player = null
    }
}
