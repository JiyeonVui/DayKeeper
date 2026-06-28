package com.jiyeon.daykeeper.ui.summary

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jiyeon.daykeeper.ui.theme.DayKeeperType

@Composable
fun SummaryScreen() {
    Scaffold { padding ->
        Text(
            text = "Summary",
            style = DayKeeperType.screenTitle,
            modifier = Modifier.padding(padding)
        )
    }
}
