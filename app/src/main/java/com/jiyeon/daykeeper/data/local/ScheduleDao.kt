package com.jiyeon.daykeeper.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {

    @Query("""
        SELECT * FROM schedule_items
        WHERE (daysOfWeek & :dayBit) != 0
        ORDER BY startMinute ASC
    """)
    fun observeItemsForDay(dayBit: Int): Flow<List<ScheduleItem>>

    @Query("SELECT * FROM schedule_items ORDER BY startMinute ASC")
    fun observeAll(): Flow<List<ScheduleItem>>

    @Query("SELECT * FROM schedule_items")
    suspend fun getAllOnce(): List<ScheduleItem>

    @Query("SELECT * FROM schedule_items WHERE id = :id")
    suspend fun getById(id: Long): ScheduleItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: ScheduleItem): Long

    @Delete
    suspend fun delete(item: ScheduleItem)

    @Query("DELETE FROM schedule_items WHERE id = :id")
    suspend fun deleteById(id: Long)
}