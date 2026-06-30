package com.jiyeon.daykeeper.ui.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jiyeon.daykeeper.data.ActivityLogRepository
import com.jiyeon.daykeeper.data.local.Week
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * Cấp [WeeklyReport] cho tuần hiện tại (Thứ Hai–Chủ Nhật) từ
 * [ActivityLogRepository]. Mốc tuần lấy ở thời điểm khởi tạo VM; đủ dùng vì màn
 * Báo cáo được tạo lại mỗi lần mở.
 */
class SummaryViewModel(
    logRepo: ActivityLogRepository,
) : ViewModel() {

    val report: StateFlow<WeeklyReport> =
        logRepo.observeRange(Week.currentRange())
            .map { logs -> logs.toWeeklyReport() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = WeeklyReport.EMPTY,
            )
}
