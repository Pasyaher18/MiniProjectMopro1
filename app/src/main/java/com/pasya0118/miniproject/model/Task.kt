package com.pasya0118.miniproject.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val priority: Priority,
    val category: Category,
    val isCompleted: Boolean = false,
    val isDeleted: Boolean = false
)

enum class Priority {
    HIGH, MEDIUM, LOW
}

enum class Category {
    WORK, PERSONAL, SHOPPING, OTHER
}