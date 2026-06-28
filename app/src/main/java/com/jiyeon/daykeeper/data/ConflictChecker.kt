package com.jiyeon.daykeeper.data

import com.jiyeon.daykeeper.data.local.ScheduleItem

/** Một va chạm thời gian: [conflictingItem] và các ngày trùng (bitmask). */
data class TimeConflict(
    val conflictingItem: ScheduleItem,
    val overlappingDays: Int,
)

/** Phát hiện trùng giờ giữa một item ứng viên và các item đã có. */
object ConflictChecker {

    /**
     * Trả về danh sách va chạm của [candidate] với [existing]. Bỏ qua chính nó
     * (khi sửa), bỏ qua item không chung ngày nào. Giờ coi là trùng theo kiểu
     * nửa mở: `start < otherEnd && otherStart < end`.
     */
    fun findConflicts(
        candidate: ScheduleItem,
        existing: List<ScheduleItem>,
    ): List<TimeConflict> =
        existing.mapNotNull { item ->
            if (item.id == candidate.id) return@mapNotNull null

            val sharedDays = candidate.daysOfWeek and item.daysOfWeek
            if (sharedDays == 0) return@mapNotNull null

            val timesOverlap =
                candidate.startMinute < item.endMinute &&
                    item.startMinute < candidate.endMinute

            if (timesOverlap) TimeConflict(item, sharedDays) else null
        }
}
