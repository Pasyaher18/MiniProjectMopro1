package com.pasya0118.miniproject.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ApiClient {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory()) // ✅ Moshi Kotlin adapter
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://yourapi.com/api/") // 🔁 Ganti ini dengan URL aslimu
        .addConverterFactory(MoshiConverterFactory.create(moshi)) // ✅ Moshi converter
        .build()

    val taskApiService: TaskApiService = retrofit.create(TaskApiService::class.java)
}
