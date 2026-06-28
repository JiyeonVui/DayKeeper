package com.jiyeon.daykeeper.reminder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.jiyeon.daykeeper.MainActivity
import com.jiyeon.daykeeper.R
import com.jiyeon.daykeeper.data.local.ScheduleItem
import com.jiyeon.daykeeper.data.local.todayBit
import com.jiyeon.daykeeper.util.toHourMinute

/**
 * Tạo kênh thông báo (API 26+) và hiển thị thông báo nhắc nhở. Toàn bộ chuỗi
 * hiển thị bằng tiếng Việt, đồng nhất với phần còn lại của app.
 */
object ReminderNotifier {

    const val CHANNEL_ID = "activity_reminders"
    const val EXTRA_DAY_BIT = "extra_day_bit"

    private const val CHANNEL_NAME = "Nhắc nhở hoạt động"
    private const val CHANNEL_DESC = "Thông báo khi một hoạt động bắt đầu"

    /** Tạo notification channel một lần (idempotent). Không làm gì dưới API 26. */
    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH,
        ).apply { description = CHANNEL_DESC }
        context.getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }

    /** Dựng và hiển thị thông báo cho [item]. Bỏ qua nếu thiếu quyền POST_NOTIFICATIONS. */
    fun show(context: Context, item: ScheduleItem) {
        if (!hasPostPermission(context)) return
        ensureChannel(context)

        val timeRange = "${item.startMinute.toHourMinute()} – ${item.endMinute.toHourMinute()}"
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(item.title)
            .setContentText("$timeRange · ${item.category.label}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(openTimelineIntent(context, item.id))
            .build()

        NotificationManagerCompat.from(context).notify(item.id.toInt(), notification)
    }

    /** Mở app về Timeline của ngày liên quan (ngày báo = hôm nay). */
    private fun openTimelineIntent(context: Context, itemId: Long): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_DAY_BIT, todayBit())
        }
        return PendingIntent.getActivity(
            context,
            itemId.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
    }

    private fun hasPostPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }
}
