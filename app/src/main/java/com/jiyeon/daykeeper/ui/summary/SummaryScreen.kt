package com.jiyeon.daykeeper.ui.summary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jiyeon.daykeeper.data.local.ActivityCategory
import com.jiyeon.daykeeper.ui.theme.DayKeeperColors
import com.jiyeon.daykeeper.ui.theme.DayKeeperTheme
import com.jiyeon.daykeeper.ui.theme.DayKeeperType
import com.jiyeon.daykeeper.ui.timeline.components.barColor
import kotlin.math.roundToInt

private val HORIZONTAL_PADDING = 16.dp
private val ROW_VERTICAL_PADDING = 13.dp
private val DIVIDER_THICKNESS = 0.5.dp
private val DOT_SIZE = 10.dp

@Composable
fun SummaryScreen(viewModel: SummaryViewModel) {
    val report by viewModel.report.collectAsStateWithLifecycle()
    SummaryContent(report)
}

@Composable
private fun SummaryContent(report: WeeklyReport) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = HORIZONTAL_PADDING),
        ) {
            Text(
                text = "Báo cáo tuần",
                style = DayKeeperType.screenTitle,
                color = DayKeeperColors.TextPrimary,
                modifier = Modifier.padding(top = 18.dp, bottom = 12.dp),
            )

            if (report.total == 0) {
                EmptyState()
            } else {
                CompletionCard(report)
                CategoryBreakdown(report)
            }
        }
    }
}

@Composable
private fun CompletionCard(report: WeeklyReport) {
    val percent = (report.completionRate * 100).roundToInt()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DayKeeperColors.PrimaryContainer)
            .padding(20.dp),
    ) {
        Text(
            text = "$percent%",
            style = DayKeeperType.displayTime,
            color = DayKeeperColors.OnPrimaryContainer,
        )
        Text(
            text = "hoàn thành tuần này",
            style = DayKeeperType.body,
            color = DayKeeperColors.OnPrimaryContainer,
        )
        Text(
            text = "Đã làm ${report.doneCount} · Bỏ qua ${report.skippedCount}",
            style = DayKeeperType.bodySmall,
            color = DayKeeperColors.OnPrimaryContainer,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
private fun CategoryBreakdown(report: WeeklyReport) {
    Text(
        text = "Theo loại",
        style = DayKeeperType.sectionTitle,
        color = DayKeeperColors.TextSecondary,
        modifier = Modifier.padding(top = 20.dp, bottom = 4.dp, start = 4.dp),
    )
    report.byCategory.forEachIndexed { index, stat ->
        if (index > 0) {
            HorizontalDivider(
                thickness = DIVIDER_THICKNESS,
                color = DayKeeperColors.Divider,
                modifier = Modifier.padding(horizontal = 4.dp),
            )
        }
        CategoryRow(stat)
    }
}

@Composable
private fun CategoryRow(stat: CategoryStat) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = ROW_VERTICAL_PADDING),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(DOT_SIZE)
                    .clip(CircleShape)
                    .background(stat.category.barColor()),
            )
            Text(
                text = stat.category.label,
                style = DayKeeperType.body,
                color = DayKeeperColors.TextPrimary,
                modifier = Modifier.padding(start = 10.dp),
            )
        }
        Text(
            text = "${stat.done}/${stat.total} đã làm",
            style = DayKeeperType.body,
            color = DayKeeperColors.TextSecondary,
        )
    }
}

@Composable
private fun EmptyState() {
    Text(
        text = "Chưa có hoạt động nào được ghi nhận trong tuần này.",
        style = DayKeeperType.body,
        color = DayKeeperColors.TextSecondary,
        modifier = Modifier.padding(vertical = 24.dp),
    )
}

@Preview(
    name = "Summary - có dữ liệu",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=1080px,height=1920px,dpi=440",
)
@Composable
private fun SummaryContentPreview() {
    DayKeeperTheme(dynamicColor = false) {
        SummaryContent(
            WeeklyReport(
                doneCount = 9,
                skippedCount = 3,
                byCategory = listOf(
                    CategoryStat(ActivityCategory.STUDY, done = 4, skipped = 1),
                    CategoryStat(ActivityCategory.SPORT, done = 3, skipped = 0),
                    CategoryStat(ActivityCategory.WORK, done = 2, skipped = 2),
                ),
            ),
        )
    }
}
