package com.example.pawpals.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AdoptionClient {

    private const val BASE_URL = "http://10.0.2.2/pawpals_api/"

    private val logger = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .cookieJar(SessionCookieJar) // 👈 JANGAN new instance
        .addInterceptor(logger)
        .build()


    val instance: AdoptionApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AdoptionApiService::class.java)
    }
}
