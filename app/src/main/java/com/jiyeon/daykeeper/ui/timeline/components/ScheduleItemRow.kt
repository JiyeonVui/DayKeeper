package com.jiyeon.daykeeper.ui.timeline.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jiyeon.daykeeper.data.local.ScheduleItem
import com.jiyeon.daykeeper.ui.theme.DayKeeperColors
import com.jiyeon.daykeeper.ui.theme.DayKeeperType
import com.jiyeon.daykeeper.ui.theme.medium
import com.jiyeon.daykeeper.util.durationLabel
import com.jiyeon.daykeeper.util.toHourMinute

@Composable
fun ScheduleItemRow(
    item: ScheduleItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryColor = item.category.barColor()
    val textTertiary = DayKeeperColors.TextTertiary

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .heightIn(min = 64.dp)
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 10.dp)
    ) {
        // Part 1 — cột giờ + thời lượng (rộng cố định, căn phải)
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.width(52.dp)
        ) {
            Text(
                text = item.startMinute.toHourMinute(),
                style = DayKeeperType.bodyLarge.medium,
                textAlign = TextAlign.End,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = durationLabel(item.startMinute, item.endMinute),
                style = DayKeeperType.caption,
                textAlign = TextAlign.End,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        // Part 2 — thanh màu phân loại (4dp, cao hết dòng)
        Box(
            Modifier
                .width(4.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(2.dp))
                .background(categoryColor)
        )

        // Part 3 — tên + ghi chú
        Column(Modifier.weight(1f)) {
            Text(
                text = item.title,
                style = DayKeeperType.bodyLarge.medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (item.note.isNotBlank()) {
                Text(
                    text = item.note,
                    style = DayKeeperType.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        // Part 4 — icon chuông (outlined)
        Icon(
            imageVector = if (item.reminderEnabled) Icons.Outlined.Notifications
            else Icons.Outlined.NotificationsOff,
            contentDescription = if (item.reminderEnabled) "Có nhắc" else "Tắt nhắc",
            tint = if (item.reminderEnabled) MaterialTheme.colorScheme.onSurfaceVariant
            else textTertiary,
            modifier = Modifier.size(18.dp)
        )
    }
}
