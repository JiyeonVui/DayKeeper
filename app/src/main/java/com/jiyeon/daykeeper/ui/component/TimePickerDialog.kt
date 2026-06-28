package com.jiyeon.daykeeper.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jiyeon.daykeeper.ui.theme.DayKeeperColors
import com.jiyeon.daykeeper.ui.theme.DayKeeperTheme
import com.jiyeon.daykeeper.ui.theme.DayKeeperType
import com.jiyeon.daykeeper.ui.theme.medium

/** Làm tròn phút về bội số 5 gần nhất, kẹp trong 0..55 (vd: 32 -> 30, 33 -> 35). */
fun snapTo5(minute: Int): Int = ((minute + 2) / 5 * 5).coerceIn(0, 55)

/**
 * Dialog chọn giờ (24h). Mở mặc định ở chế độ nhập số ([TimeInput]) cho nhanh,
 * có nút chuyển sang mặt đồng hồ ([TimePicker]) và ngược lại. Phút trả về đã
 * được snap về bội số 5. Stateless ngoài state đồng hồ và cờ [showInput].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onConfirm: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val state = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true,
    )
    var showInput by remember { mutableStateOf(true) }

    val pickerColors = TimePickerDefaults.colors(
        selectorColor = DayKeeperColors.Primary,
        containerColor = DayKeeperColors.PrimaryContainer,
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm(state.hour, snapTo5(state.minute)) }) {
                Text(
                    text = "Xong",
                    style = DayKeeperType.bodyLarge.medium,
                    color = DayKeeperColors.Primary,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Huỷ",
                    style = DayKeeperType.bodyLarge.medium,
                    color = DayKeeperColors.TextSecondary,
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (showInput) {
                    TimeInput(state = state, colors = pickerColors)
                } else {
                    TimePicker(state = state, colors = pickerColors)
                }
                TextButton(onClick = { showInput = !showInput }) {
                    Text(
                        text = if (showInput) "Dùng đồng hồ" else "Gõ giờ",
                        style = DayKeeperType.bodySmall.medium,
                        color = DayKeeperColors.Primary,
                    )
                }
            }
        },
    )
}

// ---- Preview ----

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "TimePickerDialog - nhập giờ", showBackground = true)
@Composable
private fun TimePickerDialogPreview() {
    DayKeeperTheme(dynamicColor = false) {
        TimePickerDialog(
            initialHour = 9,
            initialMinute = 30,
            onConfirm = { _, _ -> },
            onDismiss = {},
        )
    }
}
