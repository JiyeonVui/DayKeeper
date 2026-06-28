package com.jiyeon.daykeeper.reminder

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings

/**
 * Hỗ trợ quyền báo thức chính xác (API 31+). Quyền POST_NOTIFICATIONS được xin
 * runtime ở [com.jiyeon.daykeeper.MainActivity].
 */
object ReminderPermissions {

    /** API < 31 luôn coi như có quyền. */
    fun hasExactAlarm(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
        return context.getSystemService(AlarmManager::class.java).canScheduleExactAlarms()
    }

    /**
     * Đưa người dùng tới màn hình Cài đặt để cấp quyền báo thức chính xác.
     * Không có quyền này thì nhắc nhở có thể không nổ đúng giờ.
     */
    fun openExactAlarmSettings(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
            data = Uri.fromParts("package", context.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        runCatching { context.startActivity(intent) }
    }
}
