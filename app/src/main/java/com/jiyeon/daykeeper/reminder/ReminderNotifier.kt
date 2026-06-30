package com.jiyeon.daykeeper.reminder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.jiyeon.daykeeper.R
import com.jiyeon.daykeeper.data.local.ScheduleItem
import com.jiyeon.daykeeper.util.toHourMinute

/**
 * Tạo kênh thông báo (API 26+) và bắn thông báo báo thức toàn màn hình cho mỗi
 * nhắc nhở. Thông báo mang theo fullScreenIntent trỏ tới [AlarmActivity] nên khi
 * màn hình tắt/khoá, hệ thống mở thẳng màn báo thức (kêu + rung liên tục). Toàn bộ
 * chuỗi hiển thị bằng tiếng Việt, đồng nhất với phần còn lại của app.
 */
object ReminderNotifier {

    const val CHANNEL_ID = "activity_reminders"
    const val EXTRA_DAY_BIT = "extra_day_bit"

    private const val CHANNEL_NAME = "Nhắc nhở hoạt động"
    private const val CHANNEL_DESC = "Báo thức khi một hoạt động bắt đầu"

    /**
     * Tạo notification channel một lần (idempotent). Không làm gì dưới API 26.
     * Kênh để IMPORTANCE_HIGH (điều kiện để fullScreenIntent kích hoạt) nhưng tắt
     * âm/rung — chuông & rung lặp liên tục do [AlarmSoundPlayer] trong [AlarmActivity]
     * đảm nhận, tránh kêu hai lần.
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

    /** Dựng và hiển thị thông báo cho [item]. Bỏ qua nếu thiếu quyền POST_NOTIFICATIONS. */
    fun show(context: Context, item: ScheduleItem) {
        if (!hasPostPermission(context)) return
        ensureChannel(context)

        val timeRange = "${item.startMinute.toHourMinute()} – ${item.endMinute.toHourMinute()}"
        val alarmIntent = alarmPendingIntent(context, item)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(item.title)
            .setContentText("$timeRange · ${item.category.label}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setOngoing(true)
            .setAutoCancel(true)
            .setContentIntent(alarmIntent)
            .setFullScreenIntent(alarmIntent, true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(item.id.toInt(), notification)
        } catch (e: SecurityException) {
            // Quyền POST_NOTIFICATIONS có thể bị thu hồi giữa lúc kiểm tra và lúc hiển thị.
        }
    }

    /** Mở màn báo thức toàn màn hình cho [item] (dùng cho content + fullScreen intent). */
    private fun alarmPendingIntent(context: Context, item: ScheduleItem): PendingIntent =
        PendingIntent.getActivity(
            context,
            item.id.toInt(),
            AlarmActivity.intent(context, item),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )

    private fun hasPostPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }
}
