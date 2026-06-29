package com.jiyeon.daykeeper.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.jiyeon.daykeeper.data.ScheduleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Báo thức bị xoá khi máy khởi động lại. Receiver này lập lại lịch cho mọi item
 * sau khi nhận [Intent.ACTION_BOOT_COMPLETED].
 *
 * Receiver do hệ thống khởi tạo nên lấy repo dùng chung qua
 * [ScheduleRepository.get] — DI thủ công, không Hilt.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()
        val appContext = context.applicationContext
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val repo = ScheduleRepository.get(appContext)
                ReminderScheduler(appContext, repo).rescheduleAll()
            } finally {
                pendingResult.finish()
            }
        }
    }
}
