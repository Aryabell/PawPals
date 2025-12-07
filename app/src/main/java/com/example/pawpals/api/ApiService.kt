package com.example.pawpals.api

import com.example.pawpals.model.Member
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("login.php")
    fun login(@Field("email") email: String, @Field("password") password: String): Call<JsonObject>

    @FormUrlEncoded
    @POST("register.php")
    fun register(@Field("name") name: String, @Field("email") email: String, @Field("password") password: String): Call<JsonObject>

    @GET("get_members.php")
    fun getMembers(): Call<JsonObject>

    @FormUrlEncoded
    @POST("add_member.php")
    fun addMember(@Field("name") name: String, @Field("email") email: String, @Field("password") password: String, @Field("role") role: String): Call<JsonObject>

    @FormUrlEncoded
    @POST("update_role.php")
    fun updateRole(@Field("id") id: Int, @Field("role") role: String): Call<JsonObject>

    @FormUrlEncoded
    @POST("block_member.php")
    fun blockMember(@Field("id") id: Int): Call<JsonObject>

    @FormUrlEncoded
    @POST("delete_member.php")
    fun deleteMember(@Field("id") id: Int): Call<JsonObject>
}
