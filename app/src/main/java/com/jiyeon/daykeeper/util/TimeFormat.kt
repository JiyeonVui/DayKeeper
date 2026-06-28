package com.jiyeon.daykeeper.util

// 420 -> "07:00"
fun Int.toHourMinute(): String = "%02d:%02d".format(this / 60, this % 60)

fun durationLabel(startMinute: Int, endMinute: Int) : String{
    val mins = (endMinute - startMinute).coerceAtLeast(0)
    val h = mins / 60
    val m = mins % 60
    return when {
        h == 0 -> "$m ph"
        m == 0 -> "$h giờ"
        else -> "${h}g${m}"
    }
}