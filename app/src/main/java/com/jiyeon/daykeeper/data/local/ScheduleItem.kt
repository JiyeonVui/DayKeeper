package com.jiyeon.daykeeper.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedule_items")
data class ScheduleItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val note: String = "",
    val startMinute: Int,
    val endMinute: Int,
    val daysOfWeek: Int,
    val reminderEnabled: Boolean = true,
    // Cố ý KHÔNG dùng: nhắc nhở luôn nổ đúng startMinute (offset bị bỏ qua khi
    // tính lịch). Giữ field để không phá schema; không có UI cho nó.
    val reminderOffsetMin: Int = 0,
    val category: ActivityCategory = ActivityCategory.DEFAULT
)