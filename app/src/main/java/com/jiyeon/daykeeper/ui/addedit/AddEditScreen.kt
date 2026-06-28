package com.jiyeon.daykeeper.ui.addedit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jiyeon.daykeeper.data.local.ActivityCategory
import com.jiyeon.daykeeper.ui.theme.DayKeeperColors
import com.jiyeon.daykeeper.ui.theme.DayKeeperTheme
import com.jiyeon.daykeeper.ui.theme.DayKeeperType
import com.jiyeon.daykeeper.ui.theme.medium
import com.jiyeon.daykeeper.ui.timeline.components.barColor
import com.jiyeon.daykeeper.ui.timeline.components.onSoftFillColor
import com.jiyeon.daykeeper.ui.timeline.components.softFillColor
import java.time.DayOfWeek

// Bo góc & viền mảnh dùng chung cho các ô trong form (RadiusMedium = 8dp).
private val RadiusMedium = 8.dp
private val FieldShape = RoundedCornerShape(RadiusMedium)

private val repeatDayLabels: List<Pair<DayOfWeek, String>> = listOf(
    DayOfWeek.MONDAY to "T2",
    DayOfWeek.TUESDAY to "T3",
    DayOfWeek.WEDNESDAY to "T4",
    DayOfWeek.THURSDAY to "T5",
    DayOfWeek.FRIDAY to "T6",
    DayOfWeek.SATURDAY to "T7",
    DayOfWeek.SUNDAY to "CN",
)

/**
 * Màn Thêm/Sửa hoạt động — toàn màn hình, dùng chung cho cả hai chế độ;
 * chỉ tiêu đề và giá trị điền sẵn khác nhau. Stateless: mọi state được hoist
 * ra ngoài qua tham số + lambda. Không xử lý lưu/điều hướng/dialog ở đây.
 */
@Composable
fun AddEditScreen(
    isEditing: Boolean,
    name: String,
    onNameChange: (String) -> Unit,
    selectedCategory: ActivityCategory,
    onCategoryChange: (ActivityCategory) -> Unit,
    startTime: String,
    endTime: String,
    onPickStart: () -> Unit,
    onPickEnd: () -> Unit,
    repeatDays: Set<DayOfWeek>,
    onToggleDay: (DayOfWeek) -> Unit,
    reminderEnabled: Boolean,
    onReminderChange: (Boolean) -> Unit,
    note: String,
    onNoteChange: (String) -> Unit,
    onClose: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopBar(isEditing = isEditing, onClose = onClose, onSave = onSave)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            NameField(name = name, onNameChange = onNameChange)
            CategoryPicker(selected = selectedCategory, onSelect = onCategoryChange)
            TimeRow(
                startTime = startTime,
                endTime = endTime,
                onPickStart = onPickStart,
                onPickEnd = onPickEnd
            )
            RepeatDays(repeatDays = repeatDays, onToggleDay = onToggleDay)
            ReminderToggle(enabled = reminderEnabled, onChange = onReminderChange)
            NoteField(note = note, onNoteChange = onNoteChange)
        }
    }
}

// ---- Top bar ----

@Composable
private fun TopBar(
    isEditing: Boolean,
    onClose: () -> Unit,
    onSave: () -> Unit,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Đóng",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(24.dp)
                    .clickable(onClick = onClose)
            )
            Text(
                text = if (isEditing) "Sửa hoạt động" else "Hoạt động mới",
                style = DayKeeperType.bodyLarge.medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Lưu",
                style = DayKeeperType.bodyLarge.medium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(onClick = onSave)
            )
        }
        HorizontalDivider(
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

// ---- Reusable label ----

@Composable
private fun FieldLabel(text: String, bottomMargin: Dp) {
    Text(
        text = text,
        style = DayKeeperType.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = bottomMargin)
    )
}

// ---- 1. Name ----

@Composable
private fun NameField(name: String, onNameChange: (String) -> Unit) {
    Column {
        FieldLabel("Tên hoạt động", bottomMargin = 6.dp)
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            singleLine = true,
            textStyle = DayKeeperType.body,
            shape = FieldShape,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ---- 2. Category picker (2×2) ----

@Composable
private fun CategoryPicker(
    selected: ActivityCategory,
    onSelect: (ActivityCategory) -> Unit,
) {
    Column {
        FieldLabel("Loại", bottomMargin = 8.dp)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ActivityCategory.entries.chunked(2).forEach { rowCategories ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    rowCategories.forEach { category ->
                        CategoryChip(
                            category = category,
                            selected = category == selected,
                            onClick = { onSelect(category) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.CategoryChip(
    category: ActivityCategory,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val barColor = category.barColor()
    val background =
        if (selected) category.softFillColor() else MaterialTheme.colorScheme.surfaceVariant
    val textColor =
        if (selected) category.onSoftFillColor() else MaterialTheme.colorScheme.onSurfaceVariant
    val borderModifier =
        if (selected) Modifier.border(2.dp, barColor, FieldShape) else Modifier

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
        modifier = Modifier
            .weight(1f)
            .clip(FieldShape)
            .background(background)
            .then(borderModifier)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    ) {
        Box(
            Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(barColor)
        )
        Text(text = category.label, style = DayKeeperType.caption, color = textColor)
    }
}

// ---- 3. Time row ----

@Composable
private fun TimeRow(
    startTime: String,
    endTime: String,
    onPickStart: () -> Unit,
    onPickEnd: () -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        TimeField(label = "Bắt đầu", time = startTime, onPick = onPickStart)
        TimeField(label = "Kết thúc", time = endTime, onPick = onPickEnd)
    }
}

@Composable
private fun RowScope.TimeField(
    label: String,
    time: String,
    onPick: () -> Unit,
) {
    Column(modifier = Modifier.weight(1f)) {
        FieldLabel(label, bottomMargin = 6.dp)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .clip(FieldShape)
                .border(0.5.dp, MaterialTheme.colorScheme.outline, FieldShape)
                .clickable(onClick = onPick)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                text = time,
                style = DayKeeperType.body,
                color = MaterialTheme.colorScheme.onSurface
            )
            Icon(
                imageVector = Icons.Outlined.Schedule,
                contentDescription = null,
                tint = DayKeeperColors.TextTertiary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// ---- 4. Repeat days ----

@Composable
private fun RepeatDays(
    repeatDays: Set<DayOfWeek>,
    onToggleDay: (DayOfWeek) -> Unit,
) {
    Column {
        FieldLabel("Lặp lại", bottomMargin = 8.dp)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeatDayLabels.forEach { (day, label) ->
                DayChip(
                    label = label,
                    selected = day in repeatDays,
                    onToggle = { onToggleDay(day) }
                )
            }
        }
    }
}

@Composable
private fun RowScope.DayChip(
    label: String,
    selected: Boolean,
    onToggle: () -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(
                if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .clickable(onClick = onToggle)
    ) {
        Text(
            text = label,
            style = DayKeeperType.caption,
            color = if (selected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ---- 5. Reminder toggle ----

@Composable
private fun ReminderToggle(
    enabled: Boolean,
    onChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Bật nhắc nhở",
            style = DayKeeperType.body,
            color = MaterialTheme.colorScheme.onSurface
        )
        Switch(
            checked = enabled,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

// ---- 6. Note ----

@Composable
private fun NoteField(note: String, onNoteChange: (String) -> Unit) {
    Column {
        FieldLabel("Ghi chú", bottomMargin = 6.dp)
        OutlinedTextField(
            value = note,
            onValueChange = onNoteChange,
            textStyle = DayKeeperType.body,
            shape = FieldShape,
            placeholder = {
                Text(
                    text = "Công viên gần nhà...",
                    style = DayKeeperType.body,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 60.dp)
        )
    }
}

// ---- Preview ----

@Preview(
    name = "AddEdit - tạo mới",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=1080px,height=1920px,dpi=440"
)
@Composable
private fun AddEditScreenPreview() {
    DayKeeperTheme(dynamicColor = false) {
        AddEditScreen(
            isEditing = false,
            name = "Tập gym",
            onNameChange = {},
            selectedCategory = ActivityCategory.SPORT,
            onCategoryChange = {},
            startTime = "18:00",
            endTime = "19:00",
            onPickStart = {},
            onPickEnd = {},
            repeatDays = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
            onToggleDay = {},
            reminderEnabled = true,
            onReminderChange = {},
            note = "",
            onNoteChange = {},
            onClose = {},
            onSave = {}
        )
    }
}
