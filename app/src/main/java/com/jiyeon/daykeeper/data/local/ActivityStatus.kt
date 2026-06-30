package com.jiyeon.daykeeper.data.local

/**
 * Phản hồi của người dùng cho một lần xảy ra của hoạt động, ghi từ màn báo thức:
 * - [DONE]    bấm "Thực hiện" — đã làm hoạt động.
 * - [SKIPPED] bấm "Tắt" — bỏ qua lần này.
 */
enum class ActivityStatus(val label: String) {
    DONE("Đã làm"),
    SKIPPED("Bỏ qua"),
}
