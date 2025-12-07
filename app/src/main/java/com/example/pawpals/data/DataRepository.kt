package com.example.pawpals.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> get() = _posts

    private val repliesMap: MutableMap<String, MutableList<Reply>> = mutableMapOf()
    private val reports = mutableListOf<Report>()

    init {
        val now = System.currentTimeMillis()

        val initialPosts = listOf(
            Post(
                id = "1",
                content = "Butuh saran vet untuk kulit anjing...",
                author = "Ari",
                timestamp = now - 2 * 60 * 60 * 1000, // 2h ago
                category = "Health",
                imageUri = null,
                userRole = "Anggota",
                commentCount = 5,
                likeCount = 10,
                userAvatar = R.drawable.ic_profile_placeholder,
                isTrending = true,
                isHidden = false
            ),
            Post(
                id = "2",
                content = "Ayo ngumpul playdate minggu depan!",
                author = "Sinta",
                timestamp = now - 6 * 60 * 60 * 1000, // 6h ago
                category = "Playdate",
                imageUri = null,
                userRole = "Anggota",
                commentCount = 15,
                likeCount = 30,
                userAvatar = R.drawable.ic_profile_placeholder,
                isTrending = true,
                isHidden = false
            ),
            Post(
                id = "3",
                content = "Ada rekomendasi mainan tahan lama?",
                author = "Rizal",
                timestamp = now - 24 * 60 * 60 * 1000, // 1 day ago
                category = "Recommend",
                imageUri = null,
                userRole = "Anggota",
                commentCount = 2,
                likeCount = 5,
                userAvatar = R.drawable.ic_profile_placeholder,
                isTrending = true,
                isHidden = false
            ),
            Post(
                id = "4",
                content = "Siapa yang pakai makanan merk X? share dong",
                author = "Tia",
                timestamp = now - 26 * 60 * 60 * 1000, // 1d 2h ago
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

        _posts.value = initialPosts
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
        val newPost = Post(
            id = UUID.randomUUID().toString(),
            content = content,
            author = author,
            timestamp = System.currentTimeMillis(),
            category = category,
            imageUri = imageUri,
            userRole = userRole,
            commentCount = commentCount,
            likeCount = likeCount,
            userAvatar = userAvatar,
            isTrending = false,
            isHidden = false
        )

        val currentList = _posts.value?.toMutableList() ?: mutableListOf()
        currentList.add(0, newPost)
        _posts.value = currentList
        return newPost
    }

    // 📂 Filter
    fun getPostsByCategory(category: String): List<Post> {
        return _posts.value?.filter {
            !it.isHidden && it.category.equals(category, ignoreCase = true)
        } ?: emptyList()
    }

    fun getTrendingPosts(limit: Int = 5): List<Post> {
        return _posts.value?.filter {
            it.isTrending && !it.isHidden
        }?.take(limit) ?: emptyList()
    }

    fun getTrendingPostsByCategory(category: String, limit: Int = 5): List<Post> {
        return _posts.value?.filter {
            it.isTrending && !it.isHidden && it.category.equals(category, ignoreCase = true)
        }?.take(limit) ?: emptyList()
    }

    // 💬 Balasan
    fun addReply(postId: String, author: String, content: String, imageUri: String? = null): Reply {
        val reply = Reply(
            id = UUID.randomUUID().toString(),
            post_id = postId,
            author = author,
            content = content,
            timestamp = System.currentTimeMillis(),
            image_path = imageUri
        )
        val list = repliesMap.getOrPut(postId) { mutableListOf() }
        list.add(reply)
        return reply
    }

    fun getReplies(postId: String): MutableList<Reply> =
        repliesMap.getOrPut(postId) { mutableListOf() }

    // 🚨 Laporan
    fun reportPost(postId: String, reason: String) {
        reports.add(Report(postId, reason, System.currentTimeMillis()))
    }

    fun getReports(): List<Report> = reports

    // ⭐ Admin-only: Toggle trending
    fun toggleTrending(postId: String): Boolean {
        val list = _posts.value?.toMutableList() ?: return false
        val index = list.indexOfFirst { it.id == postId }
        if (index != -1) {
            val post = list[index]
            val updated = post.copy(isTrending = !post.isTrending)
            list[index] = updated
            _posts.value = list
            return updated.isTrending
        }
        return false
    }

    // 🙈 Admin-only: Sembunyikan post
    fun hidePost(postId: String) {
        val list = _posts.value?.toMutableList() ?: return
        val index = list.indexOfFirst { it.id == postId }
        if (index != -1) {
            val post = list[index]
            list[index] = post.copy(isHidden = true, isTrending = false)
            _posts.value = list
        }
    }

    fun unhidePost(postId: String) {
        val list = _posts.value?.toMutableList() ?: return
        val index = list.indexOfFirst { it.id == postId }
        if (index != -1) {
            val post = list[index]
            list[index] = post.copy(isHidden = false)
            _posts.value = list
        }
    }

    fun deletePost(postId: String) {
        val newList = _posts.value?.filterNot { it.id == postId } ?: return
        _posts.value = newList
    }
}
