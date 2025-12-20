package com.example.pawpals.api

import com.example.pawpals.notification.NotificationModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NotificationApiService {

    @GET("get_notifications.php")
    fun getAllNotifications(
        @Query("user_id") userId: Int
    ): Call<List<NotificationModel>>

    @GET("get_notifications.php")
    fun getNotificationsByType(
        @Query("user_id") userId: Int,
        @Query("type") type: String
    ): Call<List<NotificationModel>>
}

