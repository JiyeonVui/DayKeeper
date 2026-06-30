package com.jiyeon.daykeeper.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jiyeon.daykeeper.ui.theme.DayKeeperColors
import com.jiyeon.daykeeper.ui.theme.DayKeeperTheme
import com.jiyeon.daykeeper.ui.theme.DayKeeperType
import com.jiyeon.daykeeper.ui.timeline.components.BottomNavigationBar
import com.jiyeon.daykeeper.ui.timeline.components.TimelineTab

private val HORIZONTAL_PADDING = 16.dp
private val GROUP_GAP = 12.dp
private val ROW_VERTICAL_PADDING = 13.dp
private val DIVIDER_THICKNESS = 0.5.dp
private val CHEVRON_SIZE = 16.dp

@Composable
fun SettingsScreen(
    leadTime: String, onPickLeadTime: () -> Unit,
    alarmStyle: String, onPickAlarmStyle: () -> Unit,
    sound: String, onPickSound: () -> Unit,
    vibration: Boolean, onVibrationChange: (Boolean) -> Unit,
    reportEnabled: Boolean, onReportEnabledChange: (Boolean) -> Unit,
    reportTime: String, onPickReportTime: () -> Unit,
    onNavTimeline: () -> Unit, onNavSummary: () -> Unit,
) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selected = TimelineTab.SETTINGS,
                onSelect = { tab ->
                    when (tab) {
                        TimelineTab.TIMELINE -> onNavTimeline()
                        TimelineTab.SUMMARY -> onNavSummary()
                        TimelineTab.SETTINGS -> Unit
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = HORIZONTAL_PADDING)
        ) {
            Text(
                text = "Cài đặt",
                style = DayKeeperType.screenTitle,
                color = DayKeeperColors.TextPrimary,
                modifier = Modifier.padding(top = 18.dp, bottom = 12.dp)
            )

            SettingsGroup(header = "Nhắc nhở") {
                SettingsPickerRow("Báo trước", leadTime, onPickLeadTime)
                RowDivider()
                SettingsPickerRow("Kiểu báo thức", alarmStyle, onPickAlarmStyle)
                RowDivider()
                SettingsPickerRow("Âm thanh", sound, onPickSound)
                RowDivider()
                SettingsToggleRow("Rung", vibration, onVibrationChange)
            }

            SettingsGroup(header = "Báo cáo tuần") {
                SettingsToggleRow("Bật báo cáo", reportEnabled, onReportEnabledChange)
                RowDivider()
                SettingsPickerRow("Gửi vào", reportTime, onPickReportTime)
            }
        }
    }
}

@Composable
private fun SettingsGroup(
    header: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.padding(bottom = GROUP_GAP)) {
        Text(
            text = header,
            style = DayKeeperType.sectionTitle,
            color = DayKeeperColors.TextSecondary,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
        )
        content()
    }
}

@Composable
private fun RowDivider() {
    HorizontalDivider(
        thickness = DIVIDER_THICKNESS,
        color = DayKeeperColors.Divider,
        modifier = Modifier.padding(horizontal = 4.dp)
    )
}

@Composable
private fun SettingsPickerRow(
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = ROW_VERTICAL_PADDING),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = DayKeeperType.body,
            color = DayKeeperColors.TextPrimary
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value,
                style = DayKeeperType.body,
                color = DayKeeperColors.TextSecondary
            )
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = DayKeeperColors.TextTertiary,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(CHEVRON_SIZE)
            )
        }
    }
}

@Composable
private fun SettingsToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = ROW_VERTICAL_PADDING),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = DayKeeperType.body,
            color = DayKeeperColors.TextPrimary
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = DayKeeperColors.Primary
            )
        )
    }
}

@Preview(
    name = "Settings",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=1080px,height=1920px,dpi=440"
)
@Composable
private fun SettingsScreenPreview() {
    DayKeeperTheme(dynamicColor = false) {
        SettingsScreen(
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
