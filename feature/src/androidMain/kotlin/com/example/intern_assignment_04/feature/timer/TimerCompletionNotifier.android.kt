package com.example.intern_assignment_04.feature.timer

import android.annotation.SuppressLint
import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

private const val TIMER_CHANNEL_ID = "timer_finished_channel"
private const val TIMER_CHANNEL_NAME = "Timer"
private const val MELODY_PREVIEW_MAX_DURATION_MILLIS = 10_000L

@Composable
internal actual fun rememberTimerCompletionNotifier(): TimerCompletionNotifier {
    val context = LocalContext.current.applicationContext

    return remember(context) {
        AndroidTimerCompletionNotifier(context)
    }
}

private class AndroidTimerCompletionNotifier(
    private val context: Context,
) : TimerCompletionNotifier {

    private var mediaPlayer: MediaPlayer? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    private var stopPlaybackRunnable: Runnable? = null

    @SuppressLint("MissingPermission")
    override fun notifyTimerCompleted(title: String, body: String) {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                TIMER_CHANNEL_ID,
                TIMER_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH,
            )
            notificationManager.createNotificationChannel(channel)

            val notification = Notification.Builder(context, TIMER_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .build()

            notificationManager.notify(System.currentTimeMillis().toInt(), notification)
            return
        }

        @Suppress("DEPRECATION")
        val notification = Notification.Builder(context)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    override fun playMelodyPreview(previewUrl: String) {
        runCatching {
            clearPlayer()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(previewUrl)
                setOnPreparedListener { player ->
                    player.start()
                    scheduleAutoStop(player)
                }
                setOnCompletionListener { player ->
                    player.release()
                    if (mediaPlayer === player) {
                        mediaPlayer = null
                    }
                    cancelAutoStop()
                }
                prepareAsync()
            }
        }
    }

    private fun scheduleAutoStop(player: MediaPlayer) {
        cancelAutoStop()
        stopPlaybackRunnable = Runnable {
            if (mediaPlayer === player) {
                runCatching { player.stop() }
                player.release()
                mediaPlayer = null
            }
        }
        mainHandler.postDelayed(stopPlaybackRunnable!!, MELODY_PREVIEW_MAX_DURATION_MILLIS)
    }

    private fun cancelAutoStop() {
        stopPlaybackRunnable?.let { mainHandler.removeCallbacks(it) }
        stopPlaybackRunnable = null
    }

    private fun clearPlayer() {
        cancelAutoStop()
        mediaPlayer?.let { player ->
            runCatching { player.stop() }
            player.release()
        }
        mediaPlayer = null
    }
}
