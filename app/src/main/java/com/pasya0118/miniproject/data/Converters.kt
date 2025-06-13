package com.pasya0118.miniproject.data

import androidx.room.TypeConverter
import com.pasya0118.miniproject.model.Category
import com.pasya0118.miniproject.model.Priority

class Converters {
    @TypeConverter
    fun fromPriority(priority: Priority): String = priority.name

    @TypeConverter
    fun toPriority(value: String): Priority = Priority.valueOf(value)

    @TypeConverter
    fun fromCategory(category: Category): String = category.name

    @TypeConverter
    fun toCategory(value: String): Category = Category.valueOf(value)
}
