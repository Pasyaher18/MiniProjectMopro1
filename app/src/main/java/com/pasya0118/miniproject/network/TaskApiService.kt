package com.pasya0118.miniproject.network

import retrofit2.http.GET

interface TaskApiService {
    @GET("tasks")
    suspend fun getTasks(): List<TaskResponse>
}
