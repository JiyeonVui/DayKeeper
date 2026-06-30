package com.jiyeon.daykeeper.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityLogDao {

    /** Ghi/ghi đè phản hồi cho một item-ngày (đụng unique index → REPLACE). */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(log: ActivityLog)

    /** Các bản ghi trong khoảng ngày [start]..[end] (đơn vị epochDay, bao gồm hai đầu). */
    @Query("SELECT * FROM activity_logs WHERE epochDay BETWEEN :start AND :end")
    fun observeBetween(start: Long, end: Long): Flow<List<ActivityLog>>
}
