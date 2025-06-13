package com.pasya0118.miniproject.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ApiClient {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://yourapi.com/api/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val taskApiService: TaskApiService = retrofit.create(TaskApiService::class.java)
}
