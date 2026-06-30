package com.jiyeon.daykeeper.data.local

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

/** Tiện ích mốc tuần. Tuần bắt đầu Thứ Hai, khớp thứ tự bit trong [Weekday]. */
object Week {

    /** Khoảng epochDay [Thứ Hai..Chủ Nhật] của tuần chứa [today] (bao gồm hai đầu). */
    fun currentRange(today: LocalDate = LocalDate.now()): LongRange {
        val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val sunday = monday.plusDays(6)
        return monday.toEpochDay()..sunday.toEpochDay()
    }
}
