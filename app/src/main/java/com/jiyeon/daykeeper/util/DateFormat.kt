package com.jiyeon.daykeeper.util

import java.time.DayOfWeek
import java.time.LocalDate

private val vietnameseWeekdays = mapOf(
    DayOfWeek.MONDAY to "Thứ Hai",
    DayOfWeek.TUESDAY to "Thứ Ba",
    DayOfWeek.WEDNESDAY to "Thứ Tư",
    DayOfWeek.THURSDAY to "Thứ Năm",
    DayOfWeek.FRIDAY to "Thứ Sáu",
    DayOfWeek.SATURDAY to "Thứ Bảy",
    DayOfWeek.SUNDAY to "Chủ Nhật",
)

/** 2026-06-22 -> "Thứ Hai, 22 tháng 6". */
fun LocalDate.vietnameseFullDate(): String {
    val weekday = vietnameseWeekdays.getValue(dayOfWeek)
    return "$weekday, $dayOfMonth tháng $monthValue"
}
