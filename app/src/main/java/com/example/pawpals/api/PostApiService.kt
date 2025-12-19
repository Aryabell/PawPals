package com.example.pawpals.api

import com.example.pawpals.model.Post
import com.example.pawpals.model.Reply
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface PostApiService {

    /* ================= POSTS ================= */

    @GET("get_posts.php")
    suspend fun getPosts(
        @Query("user_id") userId: Int
    ): List<Post>

    @Multipart
    @POST("add_post.php")
    suspend fun addPost(
        @Part("content") content: RequestBody,
        @Part("author") author: RequestBody,
        @Part("category") category: RequestBody,
        @Part("user_role") userRole: RequestBody,
        @Part image: MultipartBody.Part?
    ): Response<Unit>

    /* ================= REPLIES ================= */

    @GET("get_replies.php")
    suspend fun getReplies(
        @Query("post_id") postId: String
    ): List<Reply>

    @Multipart
    @POST("add_reply.php")
    suspend fun addReply(
        @Part("post_id") postId: RequestBody,
        @Part("author") author: RequestBody,
        @Part("content") content: RequestBody,
        @Part image: MultipartBody.Part?
    ): Response<Unit>

    @FormUrlEncoded
    @POST("toggle_like.php")
    suspend fun toggleLike(
        @Field("post_id") postId: String,
        @Field("user_id") userId: Int
    ): LikeResponse
}
