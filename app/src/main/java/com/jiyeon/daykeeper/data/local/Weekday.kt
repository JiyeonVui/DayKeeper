package com.jiyeon.daykeeper.data.local

object Weekday {
    const val MON = 1
    const val TUE = 2
    const val WED = 4
    const val THU = 8
    const val FRI = 16
    const val SAT = 32
    const val SUN = 64

    // Kiểm tra item có lặp vào 1 ngày cụ thể không
    fun ScheduleItem.repeatsOn(dayBit: Int): Boolean =
        (daysOfWeek and dayBit) != 0
}