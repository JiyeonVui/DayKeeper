package com.jiyeon.daykeeper.ui.addedit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jiyeon.daykeeper.data.local.ActivityCategory
import com.jiyeon.daykeeper.data.local.ScheduleItem
import com.jiyeon.daykeeper.data.local.toBit
import com.jiyeon.daykeeper.ui.addedit.components.ConflictDialog
import com.jiyeon.daykeeper.ui.component.TimePickerDialog
import com.jiyeon.daykeeper.util.toHourMinute
import java.time.DayOfWeek

private const val DEFAULT_START_MINUTE = 7 * 60   // 07:00
private const val DEFAULT_END_MINUTE = 8 * 60      // 08:00

/**
 * Lớp giữ state cho [AddEditScreen]. Hoist toàn bộ field của form bằng `remember`,
 * khởi tạo từ [existingItem] (sửa) hoặc giá trị mặc định (tạo mới). State được
 * reset khi [existingItem] đổi (mở item khác).
 *
 * Lưu qua [AddEditViewModel.trySave]: nếu trùng giờ thì chặn và hiện
 * [ConflictDialog]; nếu không thì lưu Room và gọi [onClose].
 */
@Composable
fun AddEditHost(
    existingItem: ScheduleItem?,
    onClose: () -> Unit,
    viewModelFactory: AddEditViewModelFactory,
    modifier: Modifier = Modifier,
) {
    val viewModel: AddEditViewModel = viewModel(factory = viewModelFactory)
    val conflicts by viewModel.conflicts.collectAsStateWithLifecycle()

    var name by remember(existingItem) { mutableStateOf(existingItem?.title.orEmpty()) }
    var category by remember(existingItem) {
        mutableStateOf(existingItem?.category ?: ActivityCategory.DEFAULT)
    }
    var startMinute by remember(existingItem) {
        mutableStateOf(existingItem?.startMinute ?: DEFAULT_START_MINUTE)
    }
    var endMinute by remember(existingItem) {
        mutableStateOf(existingItem?.endMinute ?: DEFAULT_END_MINUTE)
    }
    var repeatDays by remember(existingItem) { mutableStateOf(existingItem.repeatDaySet()) }
    var reminderEnabled by remember(existingItem) {
        mutableStateOf(existingItem?.reminderEnabled ?: true)
    }
    var note by remember(existingItem) { mutableStateOf(existingItem?.note.orEmpty()) }

    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    // Dựng ScheduleItem từ state form hiện tại (giữ id & reminderOffset khi sửa).
    fun buildItem(): ScheduleItem {
        val base = existingItem
            ?: ScheduleItem(title = "", startMinute = 0, endMinute = 0, daysOfWeek = 0)
        return base.copy(
            id = existingItem?.id ?: 0L,
            title = name.trim(),
            note = note,
            startMinute = startMinute,
            endMinute = endMinute,
            daysOfWeek = repeatDays.fold(0) { acc, day -> acc or day.toBit() },
            reminderEnabled = reminderEnabled,
            category = category,
        )
    }

    AddEditScreen(
        isEditing = existingItem != null,
        name = name,
        onNameChange = { name = it },
        selectedCategory = category,
        onCategoryChange = { category = it },
        startTime = startMinute.toHourMinute(),
        endTime = endMinute.toHourMinute(),
        onPickStart = { showStartPicker = true },
        onPickEnd = { showEndPicker = true },
        repeatDays = repeatDays,
        onToggleDay = { day ->
            repeatDays = if (day in repeatDays) repeatDays - day else repeatDays + day
        },
        reminderEnabled = reminderEnabled,
        onReminderChange = { reminderEnabled = it },
        note = note,
        onNoteChange = { note = it },
        onClose = onClose,
        onSave = {
            // Validate nhẹ: tên trống hoặc giờ kết thúc <= bắt đầu thì bỏ qua.
            if (name.isNotBlank() && endMinute > startMinute) {
                viewModel.trySave(buildItem(), onSuccess = onClose)
            }
        },
        modifier = modifier,
    )

    if (conflicts.isNotEmpty()) {
        ConflictDialog(
            candidateTitle = name.trim(),
            candidateStart = startMinute,
            candidateEnd = endMinute,
            conflicts = conflicts,
            onDismiss = viewModel::dismissConflicts,
        )
    }

    if (showStartPicker) {
        TimePickerDialog(
            initialHour = startMinute / 60,
            initialMinute = startMinute % 60,
            onConfirm = { hour, minute ->
                startMinute = hour * 60 + minute
                showStartPicker = false
            },
            onDismiss = { showStartPicker = false },
        )
    }

    if (showEndPicker) {
        TimePickerDialog(
            initialHour = endMinute / 60,
            initialMinute = endMinute % 60,
            onConfirm = { hour, minute ->
                endMinute = hour * 60 + minute
                showEndPicker = false
            },
            onDismiss = { showEndPicker = false },
        )
    }
}

/** Bitmask [ScheduleItem.daysOfWeek] -> tập ngày (Thứ Hai -> Chủ Nhật). */
private fun ScheduleItem?.repeatDaySet(): Set<DayOfWeek> =
    if (this == null) emptySet()
    else DayOfWeek.entries.filterTo(LinkedHashSet()) { (daysOfWeek and it.toBit()) != 0 }
