package com.jiyeon.daykeeper.ui.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jiyeon.daykeeper.data.ActivityLogRepository

class SummaryViewModelFactory(
    private val logRepo: ActivityLogRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SummaryViewModel(logRepo) as T
    }
}
