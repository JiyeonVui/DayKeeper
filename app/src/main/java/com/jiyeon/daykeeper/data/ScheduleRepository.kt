package com.jiyeon.daykeeper.data

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
}