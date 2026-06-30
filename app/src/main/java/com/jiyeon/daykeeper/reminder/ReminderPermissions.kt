package com.jiyeon.daykeeper.reminder

import android.app.AlarmManager
import android.app.NotificationManager
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

    /**
     * Quyền báo thức toàn màn hình. Từ Android 14 (API 34), app không phải đồng hồ/
     * gọi điện bị thu hồi mặc định; thiếu quyền thì nhắc nhở chỉ hiện heads-up chứ
     * không bật màn báo thức đè màn khoá. API < 34 luôn coi như có quyền.
     */
    fun canUseFullScreenIntent(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) return true
        return context.getSystemService(NotificationManager::class.java).canUseFullScreenIntent()
    }

    /** Đưa người dùng tới Cài đặt để cấp quyền báo thức toàn màn hình (API 34+). */
    fun openFullScreenIntentSettings(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) return
        val intent = Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT).apply {
            data = Uri.fromParts("package", context.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        runCatching { context.startActivity(intent) }
    }
}
