package com.jiyeon.daykeeper.ui.addedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jiyeon.daykeeper.data.ConflictChecker
import com.jiyeon.daykeeper.data.ScheduleRepository
import com.jiyeon.daykeeper.data.TimeConflict
import com.jiyeon.daykeeper.data.local.ScheduleItem
import com.jiyeon.daykeeper.reminder.ScheduleCoordinator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Xử lý lưu item Add/Edit kèm kiểm tra trùng giờ. Khi phát hiện va chạm thì
 * KHÔNG lưu mà phơi ra [conflicts] để UI hiện cảnh báo; chỉ lưu khi không trùng.
 *
 * Lưu đi qua [ScheduleCoordinator] để báo thức được đặt lại theo cùng đường lưu
 * (không có nhánh lập lịch riêng).
 */
class AddEditViewModel(
    private val repo: ScheduleRepository,
    private val coordinator: ScheduleCoordinator,
) : ViewModel() {

    private val _conflicts = MutableStateFlow<List<TimeConflict>>(emptyList())
    val conflicts: StateFlow<List<TimeConflict>> = _conflicts.asStateFlow()

    fun trySave(candidate: ScheduleItem, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val all = repo.getAllOnce()
            val found = ConflictChecker.findConflicts(candidate, all)
            if (found.isNotEmpty()) {
                _conflicts.value = found
            } else {
                coordinator.save(candidate)
                onSuccess()
            }
        }
    }

    fun dismissConflicts() {
        _conflicts.value = emptyList()
    }
}
