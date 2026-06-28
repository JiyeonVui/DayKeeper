package com.jiyeon.daykeeper.reminder

import android.content.Context
import com.jiyeon.daykeeper.data.ScheduleRepository
import com.jiyeon.daykeeper.data.local.AppDatabase

/**
 * Service-locator tối giản cho các BroadcastReceiver (chạy ngoài Activity nên
 * không lấy được dependency đã dựng tay trong [com.jiyeon.daykeeper.MainActivity]).
 * Giữ DI thủ công, không dùng Hilt. [AppDatabase.get] đã là singleton nên việc
 * dựng lại Repository ở đây là rẻ và không trùng state.
 */
object ReminderGraph {

    fun repository(context: Context): ScheduleRepository =
        ScheduleRepository(AppDatabase.get(context).scheduleDao())

    fun scheduler(context: Context): ReminderScheduler =
        ReminderScheduler(context.applicationContext, repository(context))
}
