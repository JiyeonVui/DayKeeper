package com.jiyeon.daykeeper.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Báo thức bị xoá khi máy khởi động lại. Receiver này lập lại lịch cho mọi item
 * sau khi nhận [Intent.ACTION_BOOT_COMPLETED].
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()
        val appContext = context.applicationContext
        CoroutineScope(Dispatchers.IO).launch {
            try {
                ReminderGraph.scheduler(appContext).rescheduleAll()
            } finally {
                pendingResult.finish()
            }
        }
    }
}
