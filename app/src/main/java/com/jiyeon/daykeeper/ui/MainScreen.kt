package com.jiyeon.daykeeper.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jiyeon.daykeeper.data.ActivityLogRepository
import com.jiyeon.daykeeper.data.ScheduleRepository
import com.jiyeon.daykeeper.data.local.ActivityLog
import com.jiyeon.daykeeper.data.local.ActivityLogDao
import com.jiyeon.daykeeper.data.local.ScheduleDao
import com.jiyeon.daykeeper.data.local.ScheduleItem
import com.jiyeon.daykeeper.reminder.NoopScheduler
import com.jiyeon.daykeeper.reminder.ScheduleCoordinator
import com.jiyeon.daykeeper.ui.addedit.AddEditHost
import com.jiyeon.daykeeper.ui.addedit.AddEditViewModelFactory
import com.jiyeon.daykeeper.ui.settings.SettingsScreen
import com.jiyeon.daykeeper.ui.summary.SummaryScreen
import com.jiyeon.daykeeper.ui.summary.SummaryViewModel
import com.jiyeon.daykeeper.ui.summary.SummaryViewModelFactory
import com.jiyeon.daykeeper.ui.theme.DayKeeperTheme
import com.jiyeon.daykeeper.ui.timeline.TimelineScreen
import com.jiyeon.daykeeper.ui.timeline.TimelineViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
fun MainScreen(
    timelineViewModel: TimelineViewModel,
    addEditViewModelFactory: AddEditViewModelFactory,
    summaryViewModelFactory: SummaryViewModelFactory,
) {
    var showAddEdit by remember { mutableStateOf(false) }
    var editingItemId by remember { mutableStateOf<Long?>(null) }
    val items by timelineViewModel.items.collectAsStateWithLifecycle()

    if (showAddEdit) {
        // Sửa: tìm item đang mở trong list của ngày đang chọn. Tạo mới: null.
        val existingItem = editingItemId?.let { id -> items.firstOrNull { it.id == id } }
        AddEditHost(
            existingItem = existingItem,
            onClose = { showAddEdit = false },
            viewModelFactory = addEditViewModelFactory,
        )
    } else {
        MainPager(
            timelineViewModel = timelineViewModel,
            summaryViewModelFactory = summaryViewModelFactory,
            onAddClick = {
                editingItemId = null
                showAddEdit = true
            },
            onItemClick = { id ->
                editingItemId = id
                showAddEdit = true
            }
        )
    }
}

@Composable
fun MainPager(
    timelineViewModel: TimelineViewModel,
    summaryViewModelFactory: SummaryViewModelFactory,
    onAddClick: () -> Unit,
    onItemClick: (Long) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        when (page) {
            0 -> TimelineScreen(
                viewModel = timelineViewModel,
                onAddClick = onAddClick,
                onItemClick = onItemClick
            )
            1 -> SummaryScreen(
                viewModel = viewModel<SummaryViewModel>(factory = summaryViewModelFactory),
            )
            2 -> SettingsScreen(
                leadTime = "10 phút", onPickLeadTime = {},
                alarmStyle = "Kêu to", onPickAlarmStyle = {},
                sound = "Mặc định", onPickSound = {},
                vibration = true, onVibrationChange = {},
                reportEnabled = true, onReportEnabledChange = {},
                reportTime = "Thứ Hai, 8:00", onPickReportTime = {},
                onNavTimeline = {}, onNavSummary = {},
            )
        }
    }
}

// ---- Previews ----

/** DAO rỗng chỉ dùng để dựng ViewModel trong preview. */
private class PreviewScheduleDao : ScheduleDao {
    override fun observeItemsForDay(dayBit: Int): Flow<List<ScheduleItem>> = flowOf(emptyList())
    override fun observeAll(): Flow<List<ScheduleItem>> = flowOf(emptyList())
    override suspend fun getById(id: Long): ScheduleItem? = null
    override suspend fun getAllOnce(): List<ScheduleItem> = emptyList()
    override suspend fun upsert(item: ScheduleItem): Long = 0
    override suspend fun delete(item: ScheduleItem) = Unit
    override suspend fun deleteById(id: Long) = Unit
}

private class PreviewActivityLogDao : ActivityLogDao {
    override suspend fun upsert(log: ActivityLog) = Unit
    override fun observeBetween(start: Long, end: Long): Flow<List<ActivityLog>> = flowOf(emptyList())
}

@Preview(
    name = "MainScreen - pager",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=1080px,height=1920px,dpi=440"
)
@Composable
private fun MainScreenPreview() {
    val repo = ScheduleRepository(PreviewScheduleDao())
    val coordinator = ScheduleCoordinator(repo, NoopScheduler)
    val logRepo = ActivityLogRepository(PreviewActivityLogDao())
    DayKeeperTheme(dynamicColor = false) {
        MainScreen(
            timelineViewModel = TimelineViewModel(repo, coordinator),
            addEditViewModelFactory = AddEditViewModelFactory(repo, coordinator),
            summaryViewModelFactory = SummaryViewModelFactory(logRepo),
        )
    }
}
