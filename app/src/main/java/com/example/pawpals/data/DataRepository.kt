package com.example.pawpals.data

import com.example.pawpals.R
import com.example.pawpals.community.Post
import com.example.pawpals.community.Reply
import java.util.*

data class Report(
    val postId: String,
    val reason: String,
    val timestamp: Long
)

object DataRepository {
    val posts: MutableList<Post> = mutableListOf()
    private val repliesMap: MutableMap<String, MutableList<Reply>> = mutableMapOf()
    private val reports = mutableListOf<Report>()

    init {
        // 🐶 Contoh data awal
        posts.add(
            Post(
                id = "1",
                content = "Butuh saran vet untuk kulit anjing...",
                author = "Ari",
                timestamp = "2h ago",
                category = "Health",
                imageUri = null,
                userRole = "Anggota",
                commentCount = 5,
                likeCount = 10,
                userAvatar = R.drawable.ic_profile_placeholder,
                isTrending = true,
                isHidden = false
            )
        )
        posts.add(
            Post(
                id = "2",
                content = "Ayo ngumpul playdate minggu depan!",
                author = "Sinta",
                timestamp = "6h ago",
                category = "Playdate",
                imageUri = null,
                userRole = "Anggota",
                commentCount = 15,
                likeCount = 30,
                userAvatar = R.drawable.ic_profile_placeholder,
                isTrending = true,
                isHidden = false
            )
        )

        posts.add(
            Post(
                id = "3",
                content = "Ada rekomendasi mainan tahan lama?",
                author = "Rizal",
                timestamp = "1d ago",
                category = "Recommend",
                imageUri = null,
                userRole = "Anggota",
                commentCount = 2,
                likeCount = 5,
                userAvatar = R.drawable.ic_profile_placeholder,
                isTrending = true,
                isHidden = false
            )
        )

        posts.add(
            Post(
                id = "4",
                content = "Siapa yang pakai makanan merk X? share dong",
                author = "Tia",
                timestamp = "1d ago",
                category = "Talks",
                imageUri = null,
                userRole = "Anggota",
                commentCount = 20,
                likeCount = 55,
                userAvatar = R.drawable.ic_profile_placeholder,
                isTrending = true,
                isHidden = false
            )
        )
    }

    // 📝 Tambah post baru
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
            id, content, author, ts, category, imageUri,
            userRole, commentCount, likeCount, userAvatar,
            isTrending = false,
            isHidden = false
        )
        posts.add(0, post)
        return post
    }

    // 📂 Ambil post per kategori (hanya yang tidak disembunyikan)
    fun getPostsByCategory(category: String): List<Post> {
        return posts.filter { p ->
            !p.isHidden && p.category.equals(category, ignoreCase = true)
        }
    }

    // 🔥 Ambil post trending (tanpa yang disembunyikan)
    fun getTrendingPosts(limit: Int = 5): List<Post> {
        return posts
            .filter { it.isTrending && !it.isHidden }
            .take(limit)
    }

    // 🆕 Ambil semua post (admin view)
    fun getAllPosts(includeHidden: Boolean = true): List<Post> {
        return if (includeHidden) posts else posts.filter { !it.isHidden }
    }

    fun getTrendingPostsByCategory(category: String, limit: Int = 5): List<Post> {
        // Tampilkan hanya post trending, kategori sesuai, dan tidak disembunyikan
        return posts.filter {
            it.isTrending &&
                    !it.isHidden &&
                    it.category.equals(category, ignoreCase = true)
        }.take(limit)
    }

    // 💬 Balasan
    fun addReply(postId: String, author: String, content: String, imageUri: String? = null): Reply {
        val id = UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis().toString()
        val reply = Reply(id, author, content, timestamp, imageUri)
        val list = repliesMap.getOrPut(postId) { mutableListOf() }
        list.add(reply)
        return reply
    }

    fun getReplies(postId: String): MutableList<Reply> = repliesMap.getOrPut(postId) { mutableListOf() }

    // 🚨 Laporan
    fun reportPost(postId: String, reason: String) {
        reports.add(Report(postId, reason, System.currentTimeMillis()))
    }

    fun getReports(): List<Report> = reports

    // ⭐ Admin-only: Tandai post sebagai trending
    fun markPostTrending(postId: String) {
        posts.find { it.id == postId }?.isTrending = true
    }

    fun toggleTrending(postId: String): Boolean {
        val post = posts.find { it.id == postId }
        post?.let {
            it.isTrending = !it.isTrending
            return it.isTrending
        }
        return false
    }


    // 🙈 Admin-only: Sembunyikan post
    fun hidePost(postId: String) {
        posts.find { it.id == postId }?.apply {
            isHidden = true
            isTrending = false // optional, supaya tidak tampil di trending list user
        }
    }


    // 👁️‍🗨️ Admin-only: Tampilkan kembali post
    fun unhidePost(postId: String) {
        posts.find { it.id == postId }?.isHidden = false
    }

    // ❌ Admin-only: Hapus permanen post
    fun deletePost(postId: String) {
        posts.removeIf { it.id == postId }
    }
}
