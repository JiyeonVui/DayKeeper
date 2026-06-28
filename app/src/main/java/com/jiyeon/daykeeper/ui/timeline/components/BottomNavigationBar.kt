package com.jiyeon.daykeeper.ui.timeline.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.jiyeon.daykeeper.ui.theme.DayKeeperColors
import com.jiyeon.daykeeper.ui.theme.DayKeeperType

enum class TimelineTab(val label: String, val icon: ImageVector) {
    TIMELINE("Timeline", Icons.Outlined.CalendarMonth),
    SUMMARY("Summary", Icons.Outlined.BarChart),
    SETTINGS("Settings", Icons.Outlined.Settings),
}

@Composable
fun BottomNavigationBar(
    selected: TimelineTab,
    onSelect: (TimelineTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier.fillMaxWidth()) {
        Column {
            HorizontalDivider(thickness = 0.5.dp)
            Row(modifier = Modifier.fillMaxWidth()) {
                TimelineTab.entries.forEach { tab ->
                    NavItem(
                        tab = tab,
                        selected = tab == selected,
                        onClick = { onSelect(tab) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun NavItem(
    tab: TimelineTab,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tint = if (selected) MaterialTheme.colorScheme.primary
    else DayKeeperColors.TextTertiary

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .clickable { onClick() }
            .padding(vertical = 10.dp)
    ) {
        Icon(
            imageVector = tab.icon,
            contentDescription = tab.label,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = tab.label,
            style = DayKeeperType.micro,
            color = tint,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
