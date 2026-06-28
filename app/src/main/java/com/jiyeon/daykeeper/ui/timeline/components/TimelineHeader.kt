package com.jiyeon.daykeeper.ui.timeline.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jiyeon.daykeeper.ui.theme.DayKeeperType
import com.jiyeon.daykeeper.util.vietnameseFullDate
import java.time.LocalDate

/**
 * Header của Timeline: tiêu đề "Hôm nay" + ngày đầy đủ.
 */
@Composable
fun TimelineHeader(
    date: LocalDate,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 30.dp, bottom = 12.dp)
    ) {
        Text(
            text = "Hôm nay",
            style = DayKeeperType.screenTitle.copy(fontSize = 33.sp, lineHeight = 42.sp),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = date.vietnameseFullDate(),
            style = DayKeeperType.bodySmall.copy(fontSize = 19.5.sp, lineHeight = 27.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
