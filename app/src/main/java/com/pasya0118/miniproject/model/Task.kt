package com.pasya0118.miniproject.model

import java.util.UUID

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val priority: Priority,
    val category: Category,
    val isCompleted: Boolean = false
)

enum class Priority {
    HIGH, MEDIUM, LOW
}

enum class Category {
    WORK, PERSONAL, SHOPPING, OTHER
} 