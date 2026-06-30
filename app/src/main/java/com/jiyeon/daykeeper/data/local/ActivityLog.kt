package com.jiyeon.daykeeper.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Phản hồi cho một *lần xảy ra* của hoạt động (theo ngày), không phải theo item:
 * cùng một item lặp lại mỗi tuần nên trạng thái phải gắn với ngày cụ thể.
 *
 * - [epochDay]  ngày của lần xảy ra ([java.time.LocalDate.toEpochDay]).
 * - [category]  chụp lại tại thời điểm ghi để báo cáo không phụ thuộc item còn tồn
 *   tại hay đã đổi loại về sau.
 *
 * Unique index (itemId, epochDay) → mỗi item/ngày chỉ một bản ghi; bấm lại sẽ ghi đè.
 */
@Entity(
    tableName = "activity_logs",
    indices = [Index(value = ["itemId", "epochDay"], unique = true)],
)
data class ActivityLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val itemId: Long,
    val epochDay: Long,
    val status: ActivityStatus,
    val category: ActivityCategory,
    val loggedAt: Long = System.currentTimeMillis(),
)
