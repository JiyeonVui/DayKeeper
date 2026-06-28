package com.jiyeon.daykeeper.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.jiyeon.daykeeper.data.ScheduleRepository
import com.jiyeon.daykeeper.data.local.ScheduleItem

/**
 * Lập lịch nhắc nhở bằng [AlarmManager] báo thức chính xác, theo mô hình tự-lặp:
 * mỗi item luôn giữ đúng một báo thức one-shot trỏ tới lần xảy ra kế tiếp. Khi nổ →
 * [AlarmReceiver] hiển thị thông báo rồi gọi [schedule] lần nữa để tính mốc kế tiếp.
 *
 * requestCode của PendingIntent = `item.id.toInt()` nên việc sửa/xoá luôn tái tạo &
 * huỷ đúng PendingIntent đã đặt.
 */
class ReminderScheduler(
    context: Context,
    private val repo: ScheduleRepository,
) : ReminderScheduling {

    private val appContext = context.applicationContext
    private val alarmManager = appContext.getSystemService(AlarmManager::class.java)

    override fun schedule(item: ScheduleItem) {
        // Nhắc tắt hoặc không có mốc kế tiếp (không chọn ngày) → đảm bảo huỷ alarm cũ.
        val fireAt = NextOccurrence.nextFireMillis(item, System.currentTimeMillis())
        if (fireAt == null) {
            cancel(item.id)
            return
        }

        val pendingIntent = buildPendingIntent(item.id)
        if (canScheduleExact()) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, fireAt, pendingIntent)
        } else {
            // Thiếu quyền báo thức chính xác (API 31+): hạ xuống báo gần đúng để
            // không crash; người dùng được hướng tới Cài đặt ở tầng UI.
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, fireAt, pendingIntent)
        }
    }

    override fun cancel(itemId: Long) {
        val pendingIntent = buildPendingIntent(itemId)
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    override suspend fun rescheduleAll() {
        repo.getAllOnce().forEach { schedule(it) }
    }

    private fun buildPendingIntent(itemId: Long): PendingIntent {
        val intent = Intent(appContext, AlarmReceiver::class.java).apply {
            action = AlarmReceiver.ACTION_FIRE
            putExtra(AlarmReceiver.EXTRA_ITEM_ID, itemId)
        }
        return PendingIntent.getBroadcast(
            appContext,
            itemId.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
    }

    private fun canScheduleExact(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
}
