package com.example.pawpals.api

import com.example.pawpals.api.AdoptionResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface AdoptionApiService {

    @Multipart
    @POST("adopt_dog.php")
    fun submitAdoption(
        @Part("user_id") userId: RequestBody,
        @Part("dog_id") dogId: RequestBody,
        @Part("dog_name") dogName: RequestBody,
        @Part("adopter_name") adopterName: RequestBody,
        @Part("adopter_address") adopterAddress: RequestBody,
        @Part("adopter_phone") adopterPhone: RequestBody,
        @Part("reason") reason: RequestBody,
        @Part image: MultipartBody.Part? // optional
    ): Call<AdoptionResponse>

    @GET("get_dogs.php")
    fun getDogs(): Call<DogResponse>

    @Multipart
    @POST("add_dog.php")
    fun addDog(

        @Part("name") name: RequestBody,
        @Part("breed") breed: RequestBody,
        @Part("location") location: RequestBody,
        @Part("age") age: RequestBody,
        @Part("weight") weight: RequestBody,
        @Part("owner_id") ownerId: RequestBody,
        @Part("owner_name") ownerName: RequestBody,
        @Part("owner_phone") ownerPhone: RequestBody,
        @Part("owner_message_handle") ownerHandle: RequestBody,
        @Part image: MultipartBody.Part?
    ): Call<DogResponse>
}
