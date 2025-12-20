package com.example.pawpals.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pawpals.api.LikeResponse
import com.example.pawpals.api.PostApiClient
import com.example.pawpals.model.Post
import com.example.pawpals.model.Reply
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

object DataRepository {

    /* ================= STATE ================= */

    private val _posts = MutableLiveData<List<Post>>(emptyList())
    val posts: LiveData<List<Post>> = _posts
    private const val CURRENT_USER_ID = 1

    /* ================= POSTS ================= */

    suspend fun getPosts(): List<Post> {
        val result = PostApiClient.api.getPosts(CURRENT_USER_ID)
        _posts.postValue(result)
        return result
    }

    fun getPostsByCategory(categoryId: String): List<Post> {
        val list = _posts.value ?: emptyList()
        return if (categoryId == "talks") {
            list.filter { !it.isHidden }
        } else {
            list.filter { !it.isHidden && it.category.equals(categoryId, true) }
        }
    }

    suspend fun addPost(
        content: String,
        author: String,
        category: String,
        userRole: String,
        imageFile: File?
    ): Boolean {

        fun String.rb() =
            toRequestBody("text/plain".toMediaType())

        val imagePart = imageFile?.let {
            val mime = when {
                it.name.endsWith("png") -> "image/png"
                else -> "image/jpeg"
            }

            MultipartBody.Part.createFormData(
                "image",
                it.name,
                it.asRequestBody(mime.toMediaType())
            )
        }

        val response = PostApiClient.api.addPost(
            content = content.rb(),
            author = author.rb(),
            category = category.rb(),
            userRole = userRole.rb(),
            image = imagePart
        )

        if (response.isSuccessful) {
            // refresh list setelah post baru
            getPosts()
        }

        return response.isSuccessful
    }

    /* ================= ADMIN ACTIONS ================= */

    suspend fun toggleTrending(postId: String) {
        PostApiClient.api.toggleTrending(postId)
        getPosts() // refresh dari database
    }

    suspend fun hidePost(postId: String) {
        PostApiClient.api.setHidden(postId, 1)
        getPosts()
    }

    suspend fun unhidePost(postId: String) {
        PostApiClient.api.setHidden(postId, 0)
        getPosts()
    }


    suspend fun toggleLike(postId: String): LikeResponse {
        val response = PostApiClient.api.toggleLike(
            postId = postId,
            userId = CURRENT_USER_ID
        )

        val list = _posts.value?.toMutableList() ?: return response
        val index = list.indexOfFirst { it.id == postId }

        if (index != -1) {
            list[index] = list[index].copy(
                isLiked = response.isLiked,
                likeCount = response.like_count
            )
            _posts.postValue(list)
        }

        return response
    }


    private fun updateHidden(postId: String, hidden: Boolean) {
        val list = _posts.value?.toMutableList() ?: return
        val index = list.indexOfFirst { it.id == postId }
        if (index == -1) return

        list[index] = list[index].copy(isHidden = hidden)
        _posts.value = list
    }

    fun reportPost(postId: String, reason: String) {
        // sementara lokal / log
        // nanti bisa sambung API report
        println("POST REPORTED: $postId | reason=$reason")
    }

    /* ================= REPLIES ================= */

    suspend fun getReplies(postId: String): List<Reply> {
        return PostApiClient.api.getReplies(postId)
    }

    fun getTrendingPosts(): List<Post> {
        return posts.value
            ?.filter { it.isTrending && !it.isHidden }
            ?: emptyList()
    }

    suspend fun addReply(
        postId: String,
        author: String,
        content: String,
        imageFile: File?
    ): Boolean {

        fun String.rb() =
            toRequestBody("text/plain".toMediaType())

        val imagePart = imageFile?.let {
            val mime = when {
                it.name.endsWith("png") -> "image/png"
                else -> "image/jpeg"
            }

            MultipartBody.Part.createFormData(
                "image",
                it.name,
                it.asRequestBody(mime.toMediaType())
            )
        }


        val response = PostApiClient.api.addReply(
            postId = postId.rb(),
            author = author.rb(),
            content = content.rb(),
            image = imagePart
        )

        return response.isSuccessful
    }
}
