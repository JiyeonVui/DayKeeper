package com.jiyeon.daykeeper.data.local

import java.time.DayOfWeek
import java.time.LocalDate

fun DayOfWeek.toBit(): Int = when (this) {
    DayOfWeek.MONDAY    -> Weekday.MON
    DayOfWeek.TUESDAY   -> Weekday.TUE
    DayOfWeek.WEDNESDAY -> Weekday.WED
    DayOfWeek.THURSDAY  -> Weekday.THU
    DayOfWeek.FRIDAY    -> Weekday.FRI
    DayOfWeek.SATURDAY  -> Weekday.SAT
    DayOfWeek.SUNDAY    -> Weekday.SUN
}

fun todayBit(): Int = LocalDate.now().dayOfWeek.toBit()