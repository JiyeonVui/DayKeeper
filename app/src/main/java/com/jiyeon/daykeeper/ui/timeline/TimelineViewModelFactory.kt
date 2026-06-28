package com.jiyeon.daykeeper.ui.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jiyeon.daykeeper.data.ScheduleRepository
import com.jiyeon.daykeeper.reminder.ScheduleCoordinator

class TimelineViewModelFactory(
    private val repo: ScheduleRepository,
    private val coordinator: ScheduleCoordinator,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TimelineViewModel(repo, coordinator) as T
    }
}
