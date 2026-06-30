package com.jiyeon.daykeeper.reminder

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat

/**
 * Foreground Service sở hữu chuông + rung của báo thức và giữ thông báo
 * fullScreenIntent. Vì Service là một-instance do hệ thống quản lý, nó là nơi tự
 * nhiên để giữ tài nguyên native (`MediaPlayer`, `Vibrator`) — thay cho global
 * mutable state ở `object`. Chuông kêu liên tục tới khi nhận [ACTION_STOP], kể cả
 * khi màn hình đang mở (khác với chỉ hiện heads-up).
 *
 * Khởi động/dừng qua [start]/[stop] (gói sẵn intent) — gọi gián tiếp qua
 * [AlarmAudioController] để tầng nghiệp vụ không phụ thuộc trực tiếp vào Service.
 */
class AlarmSoundService : Service() {

    private var player: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> handleStart(intent)
            else -> handleStop()
        }
        // Không tự hồi sinh nếu bị kill: báo thức đã qua không nên kêu lại vô cớ.
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAll()
    }

    private fun handleStart(intent: Intent) {
        val notification = intent.notificationExtra()
        if (notification == null) {
            // Thiếu thông báo → không thể startForeground; thoát an toàn.
            stopForegroundAndSelf()
            return
        }
        startForeground(FGS_NOTIFICATION_ID, notification)
        stopAll()
        startSound()
        startVibration()
    }

    private fun handleStop() {
        stopAll()
        stopForegroundAndSelf()
    }

    private fun stopForegroundAndSelf() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    // ----- Chuông + rung -----

    private fun stopAll() {
        stopSound()
        stopVibration()
    }

    private fun startSound() {
        val uri = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: Settings.System.DEFAULT_ALARM_ALERT_URI
            ?: return

        runCatching {
            player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        // USAGE_ALARM → dùng âm lượng báo thức, kêu cả khi đang im lặng/DND.
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build(),
                )
                setDataSource(this@AlarmSoundService, uri)
                isLooping = true
                setOnPreparedListener { start() }
                prepareAsync()
            }
        }.onFailure {
            // Thiết bị không có âm báo thức / lỗi giải mã → vẫn còn rung báo hiệu.
            player?.release()
            player = null
        }
    }

    private fun stopSound() {
        player?.runCatching { stop() }
        player?.release()
        player = null
    }

    private fun startVibration() {
        val vib = resolveVibrator() ?: return
        if (!vib.hasVibrator()) return
        vibrator = vib

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vib.vibrate(VibrationEffect.createWaveform(VIBRATION_PATTERN, VIBRATION_REPEAT_INDEX))
        } else {
            @Suppress("DEPRECATION")
            vib.vibrate(VIBRATION_PATTERN, VIBRATION_REPEAT_INDEX)
        }
    }

    private fun stopVibration() {
        vibrator?.cancel()
        vibrator = null
    }

    private fun resolveVibrator(): Vibrator? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getSystemService(VibratorManager::class.java)?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Vibrator::class.java)
        }

    private fun Intent.notificationExtra(): Notification? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getParcelableExtra(EXTRA_NOTIFICATION, Notification::class.java)
        } else {
            @Suppress("DEPRECATION")
            getParcelableExtra(EXTRA_NOTIFICATION)
        }

    companion object {
        private const val FGS_NOTIFICATION_ID = 1001
        private const val ACTION_START = "com.jiyeon.daykeeper.reminder.ALARM_SOUND_START"
        private const val ACTION_STOP = "com.jiyeon.daykeeper.reminder.ALARM_SOUND_STOP"
        private const val EXTRA_NOTIFICATION = "alarm_notification"

        // Rung 0.6s, nghỉ 0.6s, lặp vô hạn (repeat = chỉ số 0 trong pattern).
        private val VIBRATION_PATTERN = longArrayOf(0L, 600L, 600L)
        private const val VIBRATION_REPEAT_INDEX = 0

        /** Bật báo thức: chạy foreground với [notification] + phát chuông/rung. */
        fun start(context: Context, notification: Notification) {
            val intent = Intent(context, AlarmSoundService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_NOTIFICATION, notification)
            }
            ContextCompat.startForegroundService(context, intent)
        }

        /** Tắt báo thức: dừng chuông/rung, gỡ thông báo, dừng Service. */
        fun stop(context: Context) {
            val intent = Intent(context, AlarmSoundService::class.java).apply {
                action = ACTION_STOP
            }
            // Gọi từ Activity đang hiển thị nên không vướng giới hạn start nền.
            context.startService(intent)
        }
    }
}
