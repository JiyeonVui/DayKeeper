package com.jiyeon.daykeeper.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Nhận báo thức khi một hoạt động bắt đầu: nạp lại item từ DB (lấy dữ liệu mới nhất)
 * → hiển thị thông báo → lập lịch cho lần kế tiếp của chính item đó (mô hình tự-lặp).
 *
 * Dùng [goAsync] để được phép đọc DB ngoài luồng chính trước khi receiver kết thúc.
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
                val repo = ReminderGraph.repository(appContext)
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
