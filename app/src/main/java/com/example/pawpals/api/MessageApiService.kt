package com.example.pawpals.api

import com.example.pawpals.message.ChatMessage
import com.example.pawpals.message.ChatPreview
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MessageApiService {

    @GET("get_chat_list.php")
    fun getChatList(
        @Query("user_id") userId: Int
    ): Call<List<ChatPreview>>

    @GET("get_messages.php")
    fun getMessages(
        @Query("chat_id") chatId: Int
    ): Call<List<ChatMessage>>

    @FormUrlEncoded
    @POST("send_message.php")
    fun sendMessage(
        @Field("chat_id") chatId: Int,
        @Field("sender_id") senderId: Int,
        @Field("message") message: String
    ): Call<Map<String, Any>>

    @FormUrlEncoded
    @POST("create_or_get_chat.php")
    fun createOrGetChat(
        @Field("user1_id") user1: Int,
        @Field("user2_id") user2: Int
    ): Call<Map<String, Any>>
}
