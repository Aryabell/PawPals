package com.example.pawpals.api

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AnnouncementApiService {

    @FormUrlEncoded
    @POST("send_announcement.php")
    fun sendAnnouncement(
        @Field("message") message: String
    ): Call<AnnouncementResponse>
}
