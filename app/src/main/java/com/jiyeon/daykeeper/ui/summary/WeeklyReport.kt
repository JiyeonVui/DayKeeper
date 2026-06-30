package com.jiyeon.daykeeper.ui.summary

import com.jiyeon.daykeeper.data.local.ActivityCategory
import com.jiyeon.daykeeper.data.local.ActivityLog
import com.jiyeon.daykeeper.data.local.ActivityStatus

/** Thống kê một loại hoạt động trong tuần. */
data class CategoryStat(
    val category: ActivityCategory,
    val done: Int,
    val skipped: Int,
) {
    val total: Int get() = done + skipped
}

/** Tổng hợp phản hồi hoạt động trong tuần để hiển thị ở Báo cáo tuần. */
data class WeeklyReport(
    val doneCount: Int,
    val skippedCount: Int,
    val byCategory: List<CategoryStat>,
) {
    val total: Int get() = doneCount + skippedCount

    /** Tỉ lệ hoàn thành 0..1 (0 khi chưa có phản hồi nào). */
    val completionRate: Float get() = if (total == 0) 0f else doneCount.toFloat() / total

    companion object {
        val EMPTY = WeeklyReport(doneCount = 0, skippedCount = 0, byCategory = emptyList())
    }
}

/** Gom danh sách [ActivityLog] của tuần thành [WeeklyReport]. */
fun List<ActivityLog>.toWeeklyReport(): WeeklyReport {
    if (isEmpty()) return WeeklyReport.EMPTY

    val byCategory = groupBy { it.category }
        .map { (category, logs) ->
            CategoryStat(
                category = category,
                done = logs.count { it.status == ActivityStatus.DONE },
                skipped = logs.count { it.status == ActivityStatus.SKIPPED },
            )
        }
        // Thứ tự ổn định theo khai báo enum để UI không nhảy.
        .sortedBy { it.category.ordinal }

    return WeeklyReport(
        doneCount = count { it.status == ActivityStatus.DONE },
        skippedCount = count { it.status == ActivityStatus.SKIPPED },
        byCategory = byCategory,
    )
}
