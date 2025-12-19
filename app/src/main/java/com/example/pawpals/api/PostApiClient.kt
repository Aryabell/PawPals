package com.example.pawpals.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object PostApiClient {

    private const val BASE_URL = "http://10.0.2.2/pawpals_api/"

    val api: PostApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PostApiService::class.java)
    }
}
