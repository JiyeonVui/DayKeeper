package com.jiyeon.daykeeper.reminder

import com.jiyeon.daykeeper.data.local.ScheduleItem

/**
 * Trừu tượng điều khiển âm báo thức (chuông + rung + thông báo toàn màn hình). Tách
 * interface để tầng gọi (AlarmReceiver, AlarmActivity) không phụ thuộc trực tiếp vào
 * [AlarmSoundService] — đồng bộ với cách [ReminderScheduling] tách khỏi AlarmManager,
 * và cho phép thay [NoopAlarmAudioController] khi test/preview.
 */
interface AlarmAudioController {

    /** Bật báo thức cho [item]: hiện màn báo toàn màn hình + kêu/rung liên tục. */
    fun start(item: ScheduleItem)

    /** Tắt báo thức đang kêu. */
    fun stop()
}

/** Bản rỗng cho @Preview / test — không chạm tới Service. */
object NoopAlarmAudioController : AlarmAudioController {
    override fun start(item: ScheduleItem) = Unit
    override fun stop() = Unit
}
