package com.jiyeon.daykeeper.ui.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jiyeon.daykeeper.ui.theme.DayKeeperType

@Composable
fun SettingsScreen() {
    Scaffold { padding ->
        Text(
            text = "Settings",
            style = DayKeeperType.screenTitle,
            modifier = Modifier.padding(padding)
        )
    }
}
