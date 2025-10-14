package com.example.pawpals.data

import com.example.pawpals.R
import com.example.pawpals.community.Post
import com.example.pawpals.community.Reply
import java.util.Locale
import java.util.UUID

data class Report(
    val postId: String,
    val reason: String,
    val timestamp: Long
)

object DataRepository {
    val posts: MutableList<Post> = mutableListOf()
    private val repliesMap: MutableMap<String, MutableList<Reply>> = mutableMapOf()

    // 🆕 Tambahan: List untuk laporan post
    private val reports = mutableListOf<Report>()

    init {
        // contoh sample post
        posts.add(0, Post(
            id = "1",
            content = "Butuh saran vet untuk kulit anjing...",
            author = "Ari",
            timestamp = "2h ago",
            category = "Health",
            imageUri = null,
            userRole = "Anggota",
            commentCount = 5,
            likeCount = 10,
            userAvatar = R.drawable.ic_profile_placeholder
        ))
        posts.add(1, Post(
            id = "2",
            content = "Ayo ngumpul playdate minggu depan!",
            author = "Sinta",
            timestamp = "6h ago",
            category = "Playdate",
            imageUri = null,
            userRole = "Anggota",
            commentCount = 15,
            likeCount = 30,
            userAvatar = R.drawable.ic_profile_placeholder
        ))
        posts.add(2, Post(
            id = "3",
            content = "Ada rekomendasi mainan tahan lama?",
            author = "Rizal",
            timestamp = "1d ago",
            category = "Recommend",
            imageUri = null,
            userRole = "Anggota",
            commentCount = 2,
            likeCount = 5,
            userAvatar = R.drawable.ic_profile_placeholder
        ))
        posts.add(3, Post(
            id = "4",
            content = "Siapa yang pakai makanan merk X? share dong",
            author = "Tia",
            timestamp = "1d ago",
            category = "Talks",
            imageUri = null,
            userRole = "Anggota",
            commentCount = 20,
            likeCount = 55,
            userAvatar = R.drawable.ic_profile_placeholder
        ))
    }

    fun addPost(
        content: String,
        author: String = "Anon",
        category: String = "Talks",
        imageUri: String? = null,
        userRole: String = "Anggota",
        commentCount: Int = 0,
        likeCount: Int = 0,
        userAvatar: Int = R.drawable.ic_profile_placeholder
    ): Post {

        val id = UUID.randomUUID().toString()
        val ts = System.currentTimeMillis().toString()

        val post = Post(
            id = id,
            content = content,
            author = author,
            timestamp = ts,
            category = category,
            imageUri = imageUri,
            userRole = userRole,
            commentCount = commentCount,
            likeCount = likeCount,
            userAvatar = userAvatar
        )
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
        val timestamp = System.currentTimeMillis().toString()

        val reply = Reply(id, author, content, timestamp, imageUri)
        val list = repliesMap.getOrPut(postId) { mutableListOf() }
        list.add(reply)
        return reply
    }

    fun getReplies(postId: String): MutableList<Reply> {
        return repliesMap.getOrPut(postId) { mutableListOf() }
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

    // 🆕 Fungsi untuk lapor post
    fun reportPost(postId: String, reason: String) {
        reports.add(Report(postId, reason, System.currentTimeMillis()))
        // 📡 nanti di sini bisa diganti simpan ke server/Firebase
    }

    // 🆕 Fungsi untuk ambil semua laporan
    fun getReports(): List<Report> = reports
}

