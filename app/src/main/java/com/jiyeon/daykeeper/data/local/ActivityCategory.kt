package com.jiyeon.daykeeper.data.local

/**
 * Phân loại hoạt động. Màu hiển thị được suy ra từ category này
 * (xem [com.jiyeon.daykeeper.ui.timeline.components.barColor]),
 * không bao giờ chọn màu thủ công.
 */
enum class ActivityCategory(val label: String) {
    REST("Nghỉ ngơi"),
    STUDY("Học tập"),
    SPORT("Thể thao"),
    WORK("Công việc");

    companion object {
        val DEFAULT = STUDY
    }
}
