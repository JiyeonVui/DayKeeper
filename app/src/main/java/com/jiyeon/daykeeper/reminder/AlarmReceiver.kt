package com.jiyeon.daykeeper.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.jiyeon.daykeeper.data.ScheduleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Nhận báo thức khi một hoạt động bắt đầu: nạp lại item từ DB (lấy dữ liệu mới nhất)
 * → hiển thị thông báo → lập lịch cho lần kế tiếp của chính item đó (mô hình tự-lặp).
 *
 * Dùng [goAsync] để được phép đọc DB ngoài luồng chính trước khi receiver kết thúc.
 * Receiver do hệ thống khởi tạo (constructor rỗng) nên lấy repo dùng chung qua
 * [ScheduleRepository.get] — DI thủ công, không Hilt.
 */
class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_FIRE) return
        val itemId = intent.getLongExtra(EXTRA_ITEM_ID, INVALID_ID)
        if (itemId == INVALID_ID) return

        val pendingResult = goAsync()
        val appContext = context.applicationContext
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val repo = ScheduleRepository.get(appContext)
                val item = repo.getById(itemId) ?: return@launch
                ReminderNotifier.show(appContext, item)
                ReminderScheduler(appContext, repo).schedule(item)
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val ACTION_FIRE = "com.jiyeon.daykeeper.reminder.ACTION_FIRE"
        const val EXTRA_ITEM_ID = "extra_item_id"
        private const val INVALID_ID = -1L
    }
}
