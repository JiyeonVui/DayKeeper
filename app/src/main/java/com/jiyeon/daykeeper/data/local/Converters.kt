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
}
