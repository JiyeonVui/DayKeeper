package com.jiyeon.daykeeper.ui.addedit.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jiyeon.daykeeper.data.TimeConflict
import com.jiyeon.daykeeper.data.local.ActivityCategory
import com.jiyeon.daykeeper.data.local.ScheduleItem
import com.jiyeon.daykeeper.data.local.Weekday
import com.jiyeon.daykeeper.data.local.daysToText
import com.jiyeon.daykeeper.ui.theme.DayKeeperTheme
import com.jiyeon.daykeeper.ui.theme.DayKeeperType
import com.jiyeon.daykeeper.ui.theme.medium
import com.jiyeon.daykeeper.util.toHourMinute

private val ConflictCardShape = RoundedCornerShape(8.dp)

/**
 * Cảnh báo trùng giờ khi lưu. Stateless: chỉ render từ tham số, mọi quyết định
 * (đóng) do caller xử lý qua [onDismiss].
 */
@Composable
fun ConflictDialog(
    candidateTitle: String,
    candidateStart: Int,
    candidateEnd: Int,
    conflicts: List<TimeConflict>,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
            )
        },
        title = {
            Text(
                text = "Trùng thời gian",
                style = DayKeeperType.dialogTitle,
                color = MaterialTheme.colorScheme.onSurface,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Hoạt động \"$candidateTitle\" " +
                        "(${candidateStart.toHourMinute()}–${candidateEnd.toHourMinute()}) " +
                        "bị đụng với:",
                    style = DayKeeperType.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                conflicts.forEach { conflict -> ConflictCard(conflict) }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Chỉnh lại thời gian",
                    style = DayKeeperType.bodyLarge.medium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        },
    )
}

@Composable
private fun ConflictCard(conflict: TimeConflict) {
    val item = conflict.conflictingItem
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ConflictCardShape)
            .border(0.5.dp, MaterialTheme.colorScheme.outline, ConflictCardShape)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = item.title,
                style = DayKeeperType.body,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "${item.startMinute.toHourMinute()}–${item.endMinute.toHourMinute()}",
                style = DayKeeperType.body,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = "Trùng vào: ${daysToText(conflict.overlappingDays)}",
            style = DayKeeperType.bodySmall,
            color = MaterialTheme.colorScheme.error,
        )
    }
}

// ---- Preview ----

@Preview(name = "ConflictDialog", showBackground = true)
@Composable
private fun ConflictDialogPreview() {
    val gym = ScheduleItem(
        id = 2, title = "Tập gym", startMinute = 1080, endMinute = 1140,
        daysOfWeek = Weekday.MON or Weekday.WED, category = ActivityCategory.SPORT,
    )
    DayKeeperTheme(dynamicColor = false) {
        ConflictDialog(
            candidateTitle = "Họp nhóm",
            candidateStart = 1100,
            candidateEnd = 1160,
            conflicts = listOf(TimeConflict(gym, Weekday.MON or Weekday.WED)),
            onDismiss = {},
        )
    }
}
