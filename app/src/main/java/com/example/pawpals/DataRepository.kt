package com.example.pawpals

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object DataRepository {
    val posts: MutableList<Post> = mutableListOf()
    private val repliesMap: MutableMap<String, MutableList<Reply>> = mutableMapOf()

    init {
        // contoh sample post (field category adalah display name seperti "Kesehatan")
        posts.add(0, Post("1", "Butuh saran vet untuk kulit anjing...", "Ari", "2h", "Kesehatan", null))
        posts.add(1, Post("2", "Ayo ngumpul playdate minggu depan!", "Sinta", "6h", "Playdate", null))
        posts.add(2, Post("3", "Ada rekomendasi mainan tahan lama?", "Rizal", "1d", "Rekomendasi", null))
        posts.add(3, Post("4", "Siapa yang pakai makanan merk X? share dong", "Tia", "1d", "Talks", null))
    }

    fun addPost(content: String, author: String = "Anon", category: String = "Talks", imageUri: String? = null): Post {
        val id = UUID.randomUUID().toString()
        val ts = formatNow()
        val post = Post(id = id, content = content, author = author, timestamp = ts, category = category, imageUri = imageUri)
        posts.add(0, post)
        return post
    }

    fun getPostsByCategory(category: String): List<Post> {
        val mappedName = mapCategoryIdToName(category)
        return posts.filter { p ->
            p.category.equals(mappedName, ignoreCase = true)
                    || p.category.equals(category, ignoreCase = true)
        }
    }

    fun getTrendingPosts(limit: Int = 5): List<Post> {
        return posts.take(limit)
    }

    fun getTrendingPostsByCategory(categoryId: String, limit: Int = 5): List<Post> {
        val filtered = getPostsByCategory(categoryId)
        return filtered.take(limit)
    }

    fun addReply(postId: String, author: String, content: String, imageUri: String? = null): Reply {
        val id = UUID.randomUUID().toString()
        val reply = Reply(id, author, content, formatNow(), imageUri)
        val list = repliesMap.getOrPut(postId) { mutableListOf() }
        list.add(reply)
        return reply
    }

    fun getReplies(postId: String): MutableList<Reply> {
        return repliesMap.getOrPut(postId) { mutableListOf() }
    }

    private fun formatNow(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun mapCategoryIdToName(id: String): String {
        return when (id.lowercase(Locale.getDefault())) {
            "health" -> "Kesehatan"
            "talks" -> "Talks"
            "playdate" -> "Playdate"
            "reco", "recommendation", "recommendasi" -> "Rekomendasi"
            else -> id
        }
    }
}
