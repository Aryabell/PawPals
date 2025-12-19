package com.example.pawpals.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.pawpals.MainActivity
import com.example.pawpals.R
import com.example.pawpals.community.CommunityAdapter
import com.example.pawpals.community.ReplyActivity
import com.example.pawpals.data.DataRepository
import com.example.pawpals.model.Post
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var rvPosts: RecyclerView
    private lateinit var postAdapter: CommunityAdapter
    private lateinit var etSearch: EditText
    private lateinit var swipeRefresh: SwipeRefreshLayout

    private var allPosts: MutableList<Post> = mutableListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvPosts = view.findViewById(R.id.rv_posts)
        etSearch = view.findViewById(R.id.et_search_home)
        swipeRefresh = view.findViewById(R.id.swipe_refresh)

        postAdapter = CommunityAdapter(
            mutableListOf(),
            { post, position -> handleLike(post, position) },
            { post ->
                val intent = Intent(requireContext(), ReplyActivity::class.java)
                intent.putExtra("post_id", post.id)
                intent.putExtra("author", post.author)
                intent.putExtra("content", post.content)
                intent.putExtra("community_tag", post.category)
                intent.putExtra("like_count", post.likeCount)
                intent.putExtra("comment_count", post.commentCount)
                intent.putExtra("is_liked", post.isLiked)
                startActivity(intent)
            }
        )

        rvPosts.layoutManager = LinearLayoutManager(requireContext())
        rvPosts.adapter = postAdapter

        // FIRST LOAD
        loadPosts()

        // PULL TO REFRESH
        swipeRefresh.setOnRefreshListener {
            loadPosts()
        }

        // SEARCH
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterPosts(s.toString())
            }
        })
    }

    private fun loadPosts() {
        swipeRefresh.isRefreshing = true

        lifecycleScope.launch {
            try {
                val posts = DataRepository.getPosts()
                allPosts = posts.toMutableList()
                postAdapter.updateData(allPosts)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                swipeRefresh.isRefreshing = false
            }
        }
    }

    private fun handleLike(post: Post, position: Int) {
        lifecycleScope.launch {
            try {
                val res = DataRepository.toggleLike(post.id)

                post.isLiked = res.isLiked
                post.likeCount = res.like_count
                postAdapter.notifyItemChanged(position)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun filterPosts(query: String) {
        val filtered = if (query.isBlank()) allPosts
        else allPosts.filter {
            it.content.contains(query, true) ||
                    it.author.contains(query, true) ||
                    it.category.contains(query, true)
        }
        postAdapter.updateData(filtered.toMutableList())
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.title = "PawPals!"
        (activity as? MainActivity)?.binding?.toolbar?.elevation = 0f

        loadPosts()
    }
}
