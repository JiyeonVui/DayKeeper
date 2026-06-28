package com.jiyeon.daykeeper.ui.timeline.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jiyeon.daykeeper.data.local.Weekday
import com.jiyeon.daykeeper.data.local.toBit
import com.jiyeon.daykeeper.ui.theme.DayKeeperTheme
import com.jiyeon.daykeeper.ui.theme.DayKeeperType
import com.jiyeon.daykeeper.ui.theme.medium
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

data class DayTab(val label: String, val dayOfMonth: Int, val bit: Int)

private val weekdayLabels = mapOf(
    DayOfWeek.MONDAY to "T2",
    DayOfWeek.TUESDAY to "T3",
    DayOfWeek.WEDNESDAY to "T4",
    DayOfWeek.THURSDAY to "T5",
    DayOfWeek.FRIDAY to "T6",
    DayOfWeek.SATURDAY to "T7",
    DayOfWeek.SUNDAY to "CN",
)

/** Bảy ngày Thứ Hai -> Chủ Nhật của tuần chứa [reference]. */
fun weekTabsFor(reference: LocalDate): List<DayTab> {
    val monday = reference.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    return (0L until 7L).map { offset ->
        val date = monday.plusDays(offset)
        DayTab(
            label = weekdayLabels.getValue(date.dayOfWeek),
            dayOfMonth = date.dayOfMonth,
            bit = date.dayOfWeek.toBit()
        )
    }
}

@Composable
fun DayTabRow(
    selectedBit: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    today: LocalDate = LocalDate.now()
) {
    val tabs = remember(today) { weekTabsFor(today) }
    val listState = rememberLazyListState()

    Column(modifier = modifier.fillMaxWidth()) {
        LazyRow(
            state = listState,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
            contentPadding = PaddingValues(horizontal = 12.dp)
        ) {
            items(tabs) { tab ->
                val selected = tab.bit == selectedBit
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(55.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (selected) MaterialTheme.colorScheme.primaryContainer
                            else Color.Transparent
                        )
                        .clickable { onSelect(tab.bit) }
                        .padding(vertical = 12.dp)
                ) {
                    Text(
                        text = tab.label,
                        style = DayKeeperType.micro.copy(fontSize = 14.4.sp),
                        color = if (selected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    val numberStyle = DayKeeperType.bodyLarge.copy(fontSize = 19.2.sp)
                    Text(
                        text = tab.dayOfMonth.toString(),
                        style = if (selected) numberStyle.medium else numberStyle,
                        color = if (selected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }

        // Thanh scrollbar ngang — chỉ hiện khi 7 tab vượt quá bề ngang.
        HorizontalScrollbar(
            state = listState,
            modifier = Modifier
                .padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 4.dp)
                .fillMaxWidth()
        )
    }
}

/**
 * Scrollbar ngang tối giản cho [LazyRow]. Tự ẩn khi nội dung không tràn.
 * Ước lượng tổng bề rộng từ các tab (cùng kích thước) đang hiển thị.
 */
@Composable
private fun HorizontalScrollbar(
    state: LazyListState,
    modifier: Modifier = Modifier
) {
    val layoutInfo = state.layoutInfo
    val items = layoutInfo.visibleItemsInfo
    if (items.isEmpty()) return

    val itemSize = items.first().size
    val spacing = if (items.size > 1) items[1].offset - items[0].offset - itemSize else 0
    val stride = itemSize + spacing
    val totalContent = stride * layoutInfo.totalItemsCount - spacing
    val viewport = layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset
    if (totalContent <= viewport) return // không tràn -> không cần scrollbar

    val thumbFraction = viewport.toFloat() / totalContent
    val maxScroll = (totalContent - viewport).toFloat()
    val scrolled = state.firstVisibleItemIndex * stride + state.firstVisibleItemScrollOffset
    val progress = (scrolled / maxScroll).coerceIn(0f, 1f)

    BoxWithConstraints(
        modifier
            .height(12.dp)
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        val thumbWidth = maxWidth * thumbFraction
        Box(
            Modifier
                .offset(x = (maxWidth - thumbWidth) * progress)
                .width(thumbWidth)
                .fillMaxHeight()
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.outline)
        )
    }
}

// ---- Previews ----

private val previewMonday: LocalDate = LocalDate.of(2026, 6, 22) // Thứ Hai

// Bề ngang hẹp -> 7 tab tràn -> scrollbar hiện.
@Preview(name = "DayTabRow - hẹp (có scrollbar)", showBackground = true, widthDp = 320)
@Composable
private fun DayTabRowNarrowPreview() {
    DayKeeperTheme(dynamicColor = false) {
        DayTabRow(selectedBit = Weekday.MON, onSelect = {}, today = previewMonday)
    }
}

// Bề ngang rộng -> đủ chỗ 7 tab -> scrollbar tự ẩn.
@Preview(name = "DayTabRow - rộng (ẩn scrollbar)", showBackground = true, widthDp = 800)
@Composable
private fun DayTabRowWidePreview() {
    DayKeeperTheme(dynamicColor = false) {
        DayTabRow(selectedBit = Weekday.MON, onSelect = {}, today = previewMonday)
    }
}
