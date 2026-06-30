package com.jiyeon.daykeeper.reminder

import android.content.Context
import com.jiyeon.daykeeper.data.local.ScheduleItem

/**
 * Triển khai [AlarmAudioController] dựa trên [AlarmSoundService]: dựng thông báo
 * báo thức rồi khởi động/dừng foreground Service.
 */
class ServiceAlarmAudioController(context: Context) : AlarmAudioController {

    private val appContext = context.applicationContext

    override fun start(item: ScheduleItem) {
        val notification = ReminderNotifier.buildAlarmNotification(appContext, item)
        AlarmSoundService.start(appContext, notification)
    }

    override fun stop() = AlarmSoundService.stop(appContext)
}

/**
 * DI thủ công: nơi lấy [AlarmAudioController] dùng chung, đồng bộ với
 * [com.jiyeon.daykeeper.data.ScheduleRepository.get]. Receiver/Activity do hệ thống
 * khởi tạo nên lấy phụ thuộc qua service-locator thay vì constructor injection.
 */
object AlarmAudio {
    fun controller(context: Context): AlarmAudioController =
        ServiceAlarmAudioController(context)
}
