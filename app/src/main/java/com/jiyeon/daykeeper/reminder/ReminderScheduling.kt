package com.jiyeon.daykeeper.reminder

import com.jiyeon.daykeeper.data.local.ScheduleItem

/**
 * Trừu tượng lớp lập lịch nhắc nhở. Tách interface để [ScheduleCoordinator] và
 * ViewModel không phụ thuộc trực tiếp vào AlarmManager — giúp test và preview
 * (dùng [NoopScheduler]) mà không cần Context thật.
 */
interface ReminderScheduling {

    /** Đặt một báo thức chính xác trỏ tới lần báo kế tiếp của [item]. */
    fun schedule(item: ScheduleItem)

    /** Huỷ báo thức đang treo của item có id [itemId]. */
    fun cancel(itemId: Long)

    /** Lập lại lịch cho mọi item (dùng sau khi khởi động lại máy). */
    suspend fun rescheduleAll()
}

/** Bản rỗng dùng cho @Preview / test — không chạm tới hệ thống. */
object NoopScheduler : ReminderScheduling {
    override fun schedule(item: ScheduleItem) = Unit
    override fun cancel(itemId: Long) = Unit
    override suspend fun rescheduleAll() = Unit
}
