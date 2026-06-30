package com.jiyeon.daykeeper.data

import android.content.Context
import com.jiyeon.daykeeper.data.local.ActivityCategory
import com.jiyeon.daykeeper.data.local.ActivityLog
import com.jiyeon.daykeeper.data.local.ActivityLogDao
import com.jiyeon.daykeeper.data.local.ActivityStatus
import com.jiyeon.daykeeper.data.local.AppDatabase
import kotlinx.coroutines.flow.Flow

/**
 * Kho phản hồi hoạt động (đã làm / bỏ qua) theo từng lần xảy ra. Tách khỏi
 * [ScheduleRepository] vì thao tác trên bảng khác và phục vụ riêng Báo cáo tuần.
 */
class ActivityLogRepository(private val dao: ActivityLogDao) {

    /** Ghi/ghi đè phản hồi cho lần xảy ra của [itemId] vào ngày [epochDay]. */
    suspend fun log(
        itemId: Long,
        epochDay: Long,
        status: ActivityStatus,
        category: ActivityCategory,
    ) = dao.upsert(
        ActivityLog(
            itemId = itemId,
            epochDay = epochDay,
            status = status,
            category = category,
        ),
    )

    /** Các bản ghi trong khoảng ngày [range] (epochDay, bao gồm hai đầu). */
    fun observeRange(range: LongRange): Flow<List<ActivityLog>> =
        dao.observeBetween(range.first, range.last)

    companion object {
        @Volatile private var INSTANCE: ActivityLogRepository? = null

        /** Instance dùng chung cho toàn app (giống [ScheduleRepository.get]). */
        fun get(context: Context): ActivityLogRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ActivityLogRepository(AppDatabase.get(context).activityLogDao())
                    .also { INSTANCE = it }
            }
    }
}
