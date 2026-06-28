package com.jiyeon.daykeeper.reminder

import com.jiyeon.daykeeper.data.local.ScheduleItem
import com.jiyeon.daykeeper.data.local.toBit
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Tính thời điểm báo nhắc kế tiếp của một [ScheduleItem]. Hàm thuần (pure),
 * không phụ thuộc Android — vì vậy unit-test được trực tiếp trên JVM.
 *
 * Giờ báo luôn bằng [ScheduleItem.startMinute]; [ScheduleItem.reminderOffsetMin]
 * bị bỏ qua có chủ đích (xem ghi chú ở entity).
 */
object NextOccurrence {

    private const val DAYS_TO_SCAN = 7

    /**
     * Trả về epoch-millis của lần báo kế tiếp tính từ [nowMillis], hoặc `null` nếu
     * nhắc đang tắt, không chọn ngày lặp nào, hoặc không tìm thấy trong 8 ngày tới.
     *
     * Thuật toán: với d trong 0..7, lấy ngày `today + d`, lấy bit thứ trong tuần;
     * nếu item lặp vào ngày đó VÀ thời điểm `(ngày đó lúc startMinute)` đứng *sau hẳn*
     * [nowMillis] thì trả về thời điểm ấy.
     */
    fun nextFireMillis(
        item: ScheduleItem,
        nowMillis: Long,
        zone: ZoneId = ZoneId.systemDefault(),
    ): Long? {
        if (!item.reminderEnabled) return null
        if (item.daysOfWeek == 0) return null

        val fireTime: LocalTime = LocalTime.ofSecondOfDay(item.startMinute * 60L)
        val today: LocalDate = Instant.ofEpochMilli(nowMillis).atZone(zone).toLocalDate()

        for (dayOffset in 0..DAYS_TO_SCAN) {
            val date = today.plusDays(dayOffset.toLong())
            if ((item.daysOfWeek and date.dayOfWeek.toBit()) == 0) continue

            val candidateMillis = ZonedDateTime.of(date, fireTime, zone)
                .toInstant()
                .toEpochMilli()
            if (candidateMillis > nowMillis) return candidateMillis
        }
        return null
    }
}
