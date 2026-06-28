package com.jiyeon.daykeeper.data.local

import java.time.DayOfWeek

private val dayLabels: List<Pair<DayOfWeek, String>> = listOf(
    DayOfWeek.MONDAY to "T2",
    DayOfWeek.TUESDAY to "T3",
    DayOfWeek.WEDNESDAY to "T4",
    DayOfWeek.THURSDAY to "T5",
    DayOfWeek.FRIDAY to "T6",
    DayOfWeek.SATURDAY to "T7",
    DayOfWeek.SUNDAY to "CN",
)

/** Bitmask [ScheduleItem.daysOfWeek] -> chuỗi nhãn ngày, vd "T2, T4". */
fun daysToText(daysOfWeek: Int): String =
    dayLabels
        .filter { (day, _) -> (daysOfWeek and day.toBit()) != 0 }
        .joinToString(separator = ", ") { (_, label) -> label }
