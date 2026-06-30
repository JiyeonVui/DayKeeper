package com.jiyeon.daykeeper.reminder

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.jiyeon.daykeeper.R
import com.jiyeon.daykeeper.data.local.ScheduleItem
import com.jiyeon.daykeeper.util.toHourMinute

/**
 * Tạo kênh thông báo (API 26+) và dựng [Notification] báo thức toàn màn hình. Thông
 * báo mang fullScreenIntent trỏ tới [AlarmActivity] (mở màn báo đè màn khoá) và được
 * [AlarmSoundService] dùng làm thông báo foreground. Toàn bộ chuỗi bằng tiếng Việt.
 */
object ReminderNotifier {

    const val CHANNEL_ID = "activity_reminders"
    const val EXTRA_DAY_BIT = "extra_day_bit"

    private const val CHANNEL_NAME = "Nhắc nhở hoạt động"
    private const val CHANNEL_DESC = "Báo thức khi một hoạt động bắt đầu"

    /**
     * Tạo notification channel một lần (idempotent). Không làm gì dưới API 26.
     * Kênh để IMPORTANCE_HIGH (điều kiện để fullScreenIntent kích hoạt) nhưng tắt
     * âm/rung — chuông & rung lặp liên tục do [AlarmSoundService] đảm nhận, tránh
     * kêu hai lần.
     */
    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = CHANNEL_DESC
            setSound(null, null)
            enableVibration(false)
        }
        context.getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }

    /**
     * Dựng thông báo báo thức cho [item] để [AlarmSoundService] chạy foreground.
     * Không kiểm tra quyền POST_NOTIFICATIONS: Service vẫn cần một Notification để
     * gọi startForeground; nếu thiếu quyền, hệ thống tự ẩn thông báo còn báo vẫn kêu.
     */
    fun buildAlarmNotification(context: Context, item: ScheduleItem): Notification {
        ensureChannel(context)

        val timeRange = "${item.startMinute.toHourMinute()} – ${item.endMinute.toHourMinute()}"
        val alarmIntent = alarmPendingIntent(context, item)
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(item.title)
            .setContentText("$timeRange · ${item.category.label}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setOngoing(true)
            .setContentIntent(alarmIntent)
            .setFullScreenIntent(alarmIntent, true)
            .build()
    }

    /** Mở màn báo thức toàn màn hình cho [item] (dùng cho content + fullScreen intent). */
    private fun alarmPendingIntent(context: Context, item: ScheduleItem): PendingIntent =
        PendingIntent.getActivity(
            context,
            item.id.toInt(),
            AlarmActivity.intent(context, item),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
}
