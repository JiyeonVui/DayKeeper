package com.jiyeon.daykeeper.reminder

import com.jiyeon.daykeeper.data.ScheduleRepository
import com.jiyeon.daykeeper.data.local.ScheduleItem

/**
 * Lớp điều phối quanh [ScheduleRepository]: mọi thao tác ghi (lưu/xoá) đều kéo theo
 * cập nhật báo thức tương ứng. Nhờ vậy lời gọi lập lịch KHÔNG rò rỉ vào Composable —
 * ViewModel chỉ làm việc với coordinator này.
 */
class ScheduleCoordinator(
    private val repo: ScheduleRepository,
    private val scheduler: ReminderScheduling,
) {

    /**
     * Lưu [item] rồi đặt lại báo thức: huỷ cái cũ theo id, lập lịch theo bản mới
     * (dùng id thực sau khi Room cấp). Trả về item đã lưu kèm id chính xác.
     */
    suspend fun save(item: ScheduleItem): ScheduleItem {
        val id = repo.save(item)
        val saved = item.copy(id = id)
        scheduler.cancel(id)
        scheduler.schedule(saved)
        return saved
    }

    /** Xoá [item] và huỷ báo thức của nó. */
    suspend fun delete(item: ScheduleItem) {
        repo.delete(item)
        scheduler.cancel(item.id)
    }
}
