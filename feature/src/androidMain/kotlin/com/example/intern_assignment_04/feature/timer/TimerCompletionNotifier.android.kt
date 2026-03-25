package com.example.intern_assignment_04.feature.timer

import android.annotation.SuppressLint
import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
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
private const val FULL_SCREEN_REQUEST_CODE = 104

/** Возвращает Android-реализацию уведомлений таймера, привязанную к application context. */
@Composable
internal actual fun rememberTimerCompletionNotifier(): TimerCompletionNotifier {
    val context = LocalContext.current.applicationContext

    return remember(context) {
        AndroidTimerCompletionNotifier(context)
    }
}

/** Android-реализация уведомлений и проигрывания превью мелодии. */
private class AndroidTimerCompletionNotifier(
    private val context: Context,
) : TimerCompletionNotifier {

    private var mediaPlayer: MediaPlayer? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    private var stopPlaybackRunnable: Runnable? = null

    /** Показывает уведомление о завершении таймера, если есть разрешение на уведомления. */
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
            ).apply {
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                description = "Timer completion alerts"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val launchIntent = context.packageManager
            .getLaunchIntentForPackage(context.packageName)
            ?.apply {
                addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP)
                addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }

        val fullScreenPendingIntent = launchIntent?.let { intent ->
            val flags = PendingIntent.FLAG_UPDATE_CURRENT or
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
            PendingIntent.getActivity(
                context,
                FULL_SCREEN_REQUEST_CODE,
                intent,
                flags,
            )
        }

        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, TIMER_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setCategory(Notification.CATEGORY_ALARM)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .apply {
                    if (fullScreenPendingIntent != null) {
                        setContentIntent(fullScreenPendingIntent)
                        setFullScreenIntent(fullScreenPendingIntent, true)
                    }
                }
                .build()
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(context)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setCategory(Notification.CATEGORY_ALARM)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setPriority(Notification.PRIORITY_MAX)
                .apply {
                    if (fullScreenPendingIntent != null) {
                        setContentIntent(fullScreenPendingIntent)
                        setFullScreenIntent(fullScreenPendingIntent, true)
                    }
                }
                .build()
        }

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    /** Запускает воспроизведение превью и ограничивает его максимальной длительностью. */
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

    /** Останавливает воспроизведение мелодии и освобождает ресурсы плеера. */
    override fun stopPlayback() {
        clearPlayer()
    }

    /** Планирует автоостановку плеера, чтобы не играть дольше заданного лимита. */
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

    /** Отменяет ранее запланированную автоостановку плеера. */
    private fun cancelAutoStop() {
        stopPlaybackRunnable?.let { mainHandler.removeCallbacks(it) }
        stopPlaybackRunnable = null
    }

    /** Безопасно завершает текущее воспроизведение и очищает ссылку на плеер. */
    private fun clearPlayer() {
        cancelAutoStop()
        mediaPlayer?.let { player ->
            runCatching { player.stop() }
            player.release()
        }
        mediaPlayer = null
    }
}
