package com.example.pawpals.api

import com.example.pawpals.data.Event
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface EventApiService {

    @GET("get_events.php")
    fun getEvents(
        @Query("user_id") userId: Int
    ): Call<List<Event>>


    @Multipart
    @POST("add_event.php")
    fun addEventWithImage(
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("date") date: RequestBody,
        @Part("location") location: RequestBody,
        @Part image: MultipartBody.Part?
    ): Call<ApiResponse>

    @FormUrlEncoded
    @POST("delete_event.php")
    fun deleteEvent(@Field("id") id: Int): Call<ApiResponse>

    @FormUrlEncoded
    @POST("join_event.php")
    fun joinEvent(
        @Field("event_id") eventId: Int,
        @Field("user_id") userId: Int
    ): Call<ApiResponse>

    @FormUrlEncoded
    @POST("cancel_join.php")
    fun cancelJoin(
        @Field("event_id") eventId: Int,
        @Field("user_id") userId: Int
    ): Call<ApiResponse>
}
