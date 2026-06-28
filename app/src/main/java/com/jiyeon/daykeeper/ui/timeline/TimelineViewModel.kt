package com.jiyeon.daykeeper.ui.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jiyeon.daykeeper.data.ScheduleRepository
import com.jiyeon.daykeeper.data.local.ScheduleItem
import com.jiyeon.daykeeper.data.local.todayBit
import com.jiyeon.daykeeper.reminder.ScheduleCoordinator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class TimelineViewModel(
    private val repo: ScheduleRepository,
    private val coordinator: ScheduleCoordinator,
) : ViewModel() {

    private val _selectedDay = MutableStateFlow(todayBit())
    val selectedDay: StateFlow<Int> = _selectedDay.asStateFlow()

    val items: StateFlow<List<ScheduleItem>> =
        _selectedDay
            .flatMapLatest { day -> repo.itemsForDay(day) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    fun selectDay(dayBit: Int) {
        _selectedDay.value = dayBit
    }

    fun delete(item: ScheduleItem) {
        viewModelScope.launch { coordinator.delete(item) }
    }
}