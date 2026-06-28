package com.jiyeon.daykeeper

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jiyeon.daykeeper.data.ScheduleRepository
import com.jiyeon.daykeeper.data.local.AppDatabase
import com.jiyeon.daykeeper.reminder.ReminderNotifier
import com.jiyeon.daykeeper.reminder.ReminderPermissions
import com.jiyeon.daykeeper.reminder.ReminderScheduler
import com.jiyeon.daykeeper.reminder.ScheduleCoordinator
import com.jiyeon.daykeeper.ui.MainScreen
import com.jiyeon.daykeeper.ui.addedit.AddEditViewModelFactory
import com.jiyeon.daykeeper.ui.theme.DayKeeperTheme
import com.jiyeon.daykeeper.ui.timeline.TimelineViewModel
import com.jiyeon.daykeeper.ui.timeline.TimelineViewModelFactory

class MainActivity : ComponentActivity() {

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted) {
                // Bật/tắt nhắc vẫn được lưu, nhưng thông báo sẽ không hiển thị.
                Toast.makeText(
                    this,
                    "Chưa cấp quyền thông báo — nhắc nhở sẽ không hiện.",
                    Toast.LENGTH_LONG,
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Dựng dependency thủ công (sau này thay bằng Hilt).
        val db = AppDatabase.get(applicationContext)
        val repo = ScheduleRepository(db.scheduleDao())
        val scheduler = ReminderScheduler(applicationContext, repo)
        val coordinator = ScheduleCoordinator(repo, scheduler)
        val factory = TimelineViewModelFactory(repo, coordinator)
        val addEditFactory = AddEditViewModelFactory(repo, coordinator)

        ReminderNotifier.ensureChannel(applicationContext)
        ensureNotificationPermission()
        ensureExactAlarmPermission()

        // Mở từ thông báo: nhảy tới Timeline của ngày liên quan (bit thứ trong tuần).
        val dayBitFromIntent = intent.getIntExtra(ReminderNotifier.EXTRA_DAY_BIT, INVALID_DAY_BIT)

        setContent {
            DayKeeperTheme {
                val vm: TimelineViewModel = viewModel(factory = factory)
                LaunchedEffect(dayBitFromIntent) {
                    if (dayBitFromIntent != INVALID_DAY_BIT) vm.selectDay(dayBitFromIntent)
                }
                MainScreen(
                    timelineViewModel = vm,
                    addEditViewModelFactory = addEditFactory,
                )
            }
        }
    }

    /** Xin POST_NOTIFICATIONS trên API 33+ khi mở app. */
    private fun ensureNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    /** Thiếu quyền báo thức chính xác (API 31+) → giải thích & đưa tới Cài đặt. */
    private fun ensureExactAlarmPermission() {
        if (ReminderPermissions.hasExactAlarm(this)) return
        Toast.makeText(
            this,
            "Cần quyền báo thức chính xác để nhắc đúng giờ.",
            Toast.LENGTH_LONG,
        ).show()
        ReminderPermissions.openExactAlarmSettings(this)
    }

    private companion object {
        const val INVALID_DAY_BIT = -1
    }
}
