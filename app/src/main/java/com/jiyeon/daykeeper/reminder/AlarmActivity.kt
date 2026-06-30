package com.jiyeon.daykeeper.reminder

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jiyeon.daykeeper.data.ActivityLogRepository
import com.jiyeon.daykeeper.data.local.ActivityCategory
import com.jiyeon.daykeeper.data.local.ActivityStatus
import com.jiyeon.daykeeper.data.local.ScheduleItem
import com.jiyeon.daykeeper.ui.theme.DayKeeperColors
import com.jiyeon.daykeeper.ui.theme.DayKeeperTheme
import com.jiyeon.daykeeper.ui.theme.DayKeeperType
import com.jiyeon.daykeeper.util.toHourMinute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * Màn hình báo thức toàn màn hình: hiện đè lên màn khoá và bật sáng màn hình khi
 * nhắc nhở nổ. Chuông + rung do [AlarmSoundService] phát; màn này chỉ là UI — bấm
 * **Tắt** (bỏ qua) hoặc **Thực hiện** (đã làm) sẽ dừng báo (qua [AlarmAudioController])
 * và ghi phản hồi cho lần xảy ra hôm đó vào [ActivityLogRepository] để dựng Báo cáo
 * tuần. Được khởi chạy qua fullScreenIntent của thông báo nên hoạt động cả khi app
 * đã đóng.
 */
class AlarmActivity : ComponentActivity() {

    private var alarm by mutableStateOf(AlarmContent.EMPTY)

    // Ghi log là tác vụ ngắn, "bắn rồi quên" — chạy ngoài lifecycle của Activity để
    // không bị huỷ khi finish(). Quá trình đang ở foreground nên insert kịp hoàn tất.
    private val logScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showOverLockScreen()

        alarm = AlarmContent.fromIntent(intent)

        setContent {
            DayKeeperTheme {
                AlarmScreen(
                    content = alarm,
                    onSkip = { dismissWith(ActivityStatus.SKIPPED) },
                    onPerform = { dismissWith(ActivityStatus.DONE) },
                )
            }
        }
    }

    /** Báo thức kế tiếp nổ khi màn này đang hiện → cập nhật nội dung hiển thị. */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        alarm = AlarmContent.fromIntent(intent)
    }

    /** Ghi phản hồi [status] cho lần xảy ra hôm đó, dừng chuông và đóng màn báo. */
    private fun dismissWith(status: ActivityStatus) {
        recordStatus(status)
        AlarmAudio.controller(this).stop()
        finish()
    }

    private fun recordStatus(status: ActivityStatus) {
        val content = alarm
        if (content.itemId < 0) return
        val repo = ActivityLogRepository.get(applicationContext)
        logScope.launch {
            repo.log(content.itemId, content.epochDay, status, content.category)
        }
    }

    /** Đè lên màn khoá + bật sáng + giữ sáng trong khi báo. Hỗ trợ cả < API 27. */
    private fun showOverLockScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
            )
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    companion object {
        private const val EXTRA_ITEM_ID = "alarm_item_id"
        private const val EXTRA_TITLE = "alarm_title"
        private const val EXTRA_TIME_RANGE = "alarm_time_range"
        private const val EXTRA_CATEGORY = "alarm_category"
        private const val EXTRA_EPOCH_DAY = "alarm_epoch_day"

        /** Dựng Intent mở màn báo thức cho [item] (dùng trong fullScreenIntent). */
        fun intent(context: Context, item: ScheduleItem): Intent =
            Intent(context, AlarmActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(EXTRA_ITEM_ID, item.id)
                putExtra(EXTRA_TITLE, item.title)
                putExtra(
                    EXTRA_TIME_RANGE,
                    "${item.startMinute.toHourMinute()} – ${item.endMinute.toHourMinute()}",
                )
                putExtra(EXTRA_CATEGORY, item.category.name)
                // Ngày của lần xảy ra = hôm nay (báo nổ đúng startMinute hôm nay).
                putExtra(EXTRA_EPOCH_DAY, LocalDate.now().toEpochDay())
            }
    }

    /** Dữ liệu hiển thị + ghi log tối thiểu cho màn báo — tách extras khỏi DB. */
    private data class AlarmContent(
        val itemId: Long,
        val title: String,
        val timeRange: String,
        val category: ActivityCategory,
        val epochDay: Long,
    ) {
        companion object {
            val EMPTY = AlarmContent(-1L, "", "", ActivityCategory.DEFAULT, 0L)

            fun fromIntent(intent: Intent) = AlarmContent(
                itemId = intent.getLongExtra(EXTRA_ITEM_ID, -1L),
                title = intent.getStringExtra(EXTRA_TITLE).orEmpty(),
                timeRange = intent.getStringExtra(EXTRA_TIME_RANGE).orEmpty(),
                category = parseCategory(intent.getStringExtra(EXTRA_CATEGORY)),
                epochDay = intent.getLongExtra(EXTRA_EPOCH_DAY, 0L),
            )

            private fun parseCategory(value: String?): ActivityCategory =
                runCatching { ActivityCategory.valueOf(value.orEmpty()) }
                    .getOrDefault(ActivityCategory.DEFAULT)
        }
    }

    @Composable
    private fun AlarmScreen(
        content: AlarmContent,
        onSkip: () -> Unit,
        onPerform: () -> Unit,
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = DayKeeperColors.Primary) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 28.dp, vertical = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.weight(1f))

                Text(
                    text = content.timeRange,
                    style = DayKeeperType.screenTitle,
                    color = DayKeeperColors.OnPrimary,
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = content.title,
                    style = DayKeeperType.displayTime.copy(fontSize = 40.sp),
                    color = DayKeeperColors.OnPrimary,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = content.category.label,
                    style = DayKeeperType.bodyLarge,
                    color = DayKeeperColors.OnPrimary.copy(alpha = 0.85f),
                )

                Spacer(Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedButton(
                        onClick = onSkip,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = DayKeeperColors.OnPrimary,
                        ),
                    ) {
                        Text("Tắt")
                    }
                    Button(
                        onClick = onPerform,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DayKeeperColors.OnPrimary,
                            contentColor = DayKeeperColors.Primary,
                        ),
                    ) {
                        Text("Thực hiện")
                    }
                }
            }
        }
    }
}
