package com.jiyeon.daykeeper.ui.addedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jiyeon.daykeeper.data.ScheduleRepository
import com.jiyeon.daykeeper.reminder.ScheduleCoordinator

class AddEditViewModelFactory(
    private val repo: ScheduleRepository,
    private val coordinator: ScheduleCoordinator,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddEditViewModel(repo, coordinator) as T
    }
}
