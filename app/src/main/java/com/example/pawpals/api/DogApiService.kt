package com.example.pawpals.api

import retrofit2.Call
import retrofit2.http.GET

interface DogApiService {
    @GET("breeds/image/random")
    fun getRandomDog(): Call<DogResponse>
}
