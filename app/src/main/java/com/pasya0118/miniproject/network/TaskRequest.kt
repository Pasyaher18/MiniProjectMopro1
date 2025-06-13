package com.pasya0118.miniproject.network

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TaskRequest(
    val name: String,
    val description: String,
    val priority: String,
    val category: String
)
