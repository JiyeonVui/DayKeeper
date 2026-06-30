package com.jiyeon.daykeeper.reminder

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings

/**
 * Phát chuông báo thức lặp liên tục + rung cho tới khi người dùng tắt — đúng cảm
 * giác "báo thức thật" thay vì một nhịp thông báo rồi im.
 *
 * Là [object] (trạng thái toàn cục) có chủ đích: [AlarmActivity] bật ở [start] và
 * tắt ở [stop]; giữ một nguồn phát duy nhất nên báo thức kế tiếp không chồng tiếng.
 * Mọi hàm đều idempotent — gọi [stop] nhiều lần vô hại.
 */
object AlarmSoundPlayer {

    // Rung 0.6s, nghỉ 0.6s, lặp vô hạn (repeat = chỉ số 0 trong pattern).
    private val VIBRATION_PATTERN = longArrayOf(0L, 600L, 600L)
    private const val VIBRATION_REPEAT_INDEX = 0

    private var player: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    /** Bắt đầu chuông + rung. Tự dừng nguồn cũ trước để không chồng tiếng. */
    fun start(context: Context) {
        stop()
        val appContext = context.applicationContext
        startSound(appContext)
        startVibration(appContext)
    }

    /** Dừng và giải phóng chuông + rung. An toàn khi chưa từng [start]. */
    fun stop() {
        player?.runCatching { stop() }
        player?.release()
        player = null

        vibrator?.cancel()
        vibrator = null
    }

    private fun startSound(context: Context) {
        val uri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM)
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
                setDataSource(context, uri)
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

    private fun startVibration(context: Context) {
        val vib = resolveVibrator(context)
        if (vib?.hasVibrator() != true) return
        vibrator = vib

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vib.vibrate(VibrationEffect.createWaveform(VIBRATION_PATTERN, VIBRATION_REPEAT_INDEX))
        } else {
            @Suppress("DEPRECATION")
            vib.vibrate(VIBRATION_PATTERN, VIBRATION_REPEAT_INDEX)
        }
    }

    private fun resolveVibrator(context: Context): Vibrator? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(VibratorManager::class.java)
            manager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Vibrator::class.java)
        }
}
