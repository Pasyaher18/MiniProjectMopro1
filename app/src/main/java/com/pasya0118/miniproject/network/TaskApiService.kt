package com.pasya0118.miniproject.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.Response

interface TaskApiService {

    @GET("tasks")
    suspend fun getTasks(): List<TaskRequest>

    @POST("tasks")
    suspend fun createTask(@Body task: TaskRequest): Response<Unit>
}
