package com.jiyeon.daykeeper.data

import android.content.Context
import com.jiyeon.daykeeper.data.local.AppDatabase
import com.jiyeon.daykeeper.data.local.ScheduleDao
import com.jiyeon.daykeeper.data.local.ScheduleItem
import kotlinx.coroutines.flow.Flow

class ScheduleRepository(private val dao: ScheduleDao) {

    fun itemsForDay(dayBit: Int): Flow<List<ScheduleItem>> =
        dao.observeItemsForDay(dayBit)

    suspend fun save(item: ScheduleItem): Long = dao.upsert(item)

    suspend fun getAllOnce(): List<ScheduleItem> = dao.getAllOnce()

    suspend fun delete(item: ScheduleItem) = dao.delete(item)

    suspend fun getById(id: Long): ScheduleItem? = dao.getById(id)

    companion object {
        @Volatile private var INSTANCE: ScheduleRepository? = null

        /**
         * Instance dùng chung cho toàn app — tạo một lần, tái dùng về sau (giống
         * [AppDatabase.get]). Production lấy repo qua đây; test/@Preview vẫn dùng
         * constructor với DAO giả.
         */
        fun get(context: Context): ScheduleRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ScheduleRepository(AppDatabase.get(context).scheduleDao())
                    .also { INSTANCE = it }
            }
    }
}
