package com.jiyeon.daykeeper.data.local

import androidx.room.TypeConverter

/** Room TypeConverters cho các kiểu không nguyên thủy. */
class Converters {

    @TypeConverter
    fun fromCategory(category: ActivityCategory): String = category.name

    @TypeConverter
    fun toCategory(value: String): ActivityCategory =
        runCatching { ActivityCategory.valueOf(value) }
            .getOrDefault(ActivityCategory.DEFAULT)

    @TypeConverter
    fun fromStatus(status: ActivityStatus): String = status.name

    @TypeConverter
    fun toStatus(value: String): ActivityStatus =
        runCatching { ActivityStatus.valueOf(value) }
            .getOrDefault(ActivityStatus.SKIPPED)
}
