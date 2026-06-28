// ui/timeline/TimelineScreen.kt
package com.jiyeon.daykeeper.ui.timeline

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jiyeon.daykeeper.data.local.ActivityCategory
import com.jiyeon.daykeeper.data.local.ScheduleItem
import com.jiyeon.daykeeper.data.local.Weekday
import com.jiyeon.daykeeper.ui.theme.DayKeeperTheme
import com.jiyeon.daykeeper.ui.timeline.components.BottomNavigationBar
import com.jiyeon.daykeeper.ui.timeline.components.DayTabRow
import com.jiyeon.daykeeper.ui.timeline.components.ScheduleItemRow
import com.jiyeon.daykeeper.ui.timeline.components.TimelineHeader
import com.jiyeon.daykeeper.ui.timeline.components.TimelineTab
import java.time.LocalDate

@Composable
fun TimelineScreen(
    viewModel: TimelineViewModel,
    onAddClick: () -> Unit,
    onItemClick: (Long) -> Unit
) {
    val selectedDay by viewModel.selectedDay.collectAsStateWithLifecycle()
    val items by viewModel.items.collectAsStateWithLifecycle()

    TimelineContent(
        selectedDay = selectedDay,
        items = items,
        onSelectDay = viewModel::selectDay,
        onAddClick = onAddClick,
        onItemClick = onItemClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineContent(
    selectedDay: Int,
    items: List<ScheduleItem>,
    onSelectDay: (Int) -> Unit,
    onAddClick: () -> Unit,
    onItemClick: (Long) -> Unit,
    today: LocalDate = LocalDate.now()
) {
    Scaffold(
        topBar = {
            Column {
                TimelineHeader(date = today)
                DayTabRow(
                    selectedBit = selectedDay,
                    onSelect = onSelectDay,
                    today = today,
                    modifier = Modifier.padding(bottom = 14.dp)
                )
            }
        },
        bottomBar = {
            // Chỉ hiển thị trực quan; điều hướng Summary/Settings sẽ thêm sau.
            BottomNavigationBar(selected = TimelineTab.TIMELINE, onSelect = {})
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier.size(52.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Thêm mục",
                    modifier = Modifier.size(26.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { padding ->
        if (items.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Chưa có gì cho ngày này. Nhấn + để thêm.",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 12.dp),
                modifier = Modifier.padding(padding)
            ) {
                itemsIndexed(items, key = { _, item -> item.id }) { index, item ->
                    ScheduleItemRow(
                        item = item,
                        onClick = { onItemClick(item.id) }
                    )
                    if (index < items.lastIndex) {
                        HorizontalDivider(
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

// ---- Previews ----

private val sampleItems = listOf(
    ScheduleItem(
        id = 1, title = "Học tiếng Hàn", note = "Bài 12 - ngữ pháp",
        startMinute = 420, endMinute = 480, daysOfWeek = Weekday.MON,
        reminderEnabled = true, category = ActivityCategory.STUDY
    ),
    ScheduleItem(
        id = 2, title = "Tập gym", note = "",
        startMinute = 1080, endMinute = 1140, daysOfWeek = Weekday.MON,
        reminderEnabled = false, category = ActivityCategory.SPORT
    ),
    ScheduleItem(
        id = 3, title = "Họp nhóm dự án", note = "Phòng A3.05",
        startMinute = 600, endMinute = 690, daysOfWeek = Weekday.MON,
        reminderEnabled = true, category = ActivityCategory.WORK
    ),
    ScheduleItem(
        id = 4, title = "Tập gym", note = "",
        startMinute = 1080, endMinute = 1140, daysOfWeek = Weekday.MON,
        reminderEnabled = false, category = ActivityCategory.SPORT
    ),
    ScheduleItem(
        id = 5, title = "Họp nhóm dự án", note = "Phòng A3.05",
        startMinute = 600, endMinute = 690, daysOfWeek = Weekday.MON,
        reminderEnabled = true, category = ActivityCategory.WORK
    ),
)

@Preview(
    name = "Timeline - có dữ liệu",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=1080px,height=1920px,dpi=440"
)
@Composable
private fun TimelineContentPreview() {
    DayKeeperTheme(dynamicColor = false) {
        TimelineContent(
            selectedDay = Weekday.MON,
            items = sampleItems,
            onSelectDay = {},
            onAddClick = {},
            onItemClick = {}
        )
    }
}

@Preview(
    name = "Timeline - rỗng",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=1080px,height=1920px,dpi=440"
)
@Composable
private fun TimelineEmptyPreview() {
    DayKeeperTheme(dynamicColor = false) {
        TimelineContent(
            selectedDay = Weekday.MON,
            items = emptyList(),
            onSelectDay = {},
            onAddClick = {},
            onItemClick = {}
        )
    }
}
