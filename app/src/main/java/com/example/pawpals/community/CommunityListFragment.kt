package com.example.pawpals.community

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pawpals.R
import com.example.pawpals.data.DataRepository
import com.example.pawpals.model.Post
import kotlinx.coroutines.launch

class CommunityListFragment : Fragment(R.layout.fragment_community_list) {

    private lateinit var rvTrending: RecyclerView
    private lateinit var rvCategories: RecyclerView
    private lateinit var trendingAdapter: CommunityAdapter

    private var allPosts: List<Post> = emptyList()

    private val categories = listOf(
        "Health" to "health",
        "Playdate" to "playdate",
        "Talks" to "talks",
        "Recommend" to "reco"
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvTrending = view.findViewById(R.id.rvTrendingPosts)
        rvCategories = view.findViewById(R.id.rvCategories)

        // Hide header if exists
        val headerId =
            resources.getIdentifier("tvForumMainTitle", "id", requireContext().packageName)
        if (headerId != 0) {
            view.findViewById<View>(headerId)?.visibility = View.GONE
        }

        setupCategoryList()
        setupTrendingList()
        loadTrendingPosts()
    }

    /* ================= CATEGORY LIST ================= */

    private fun setupCategoryList() {
        rvCategories.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val titles = categories.map { it.first }

        rvCategories.adapter = CategoryAdapter(titles) { selectedTitle ->
            val pair = categories.find { it.first == selectedTitle }
            val categoryId = pair?.second ?: "talks"

            val frag = CommunityPostsFragment.newInstance(
                selectedTitle,
                categoryId
            )

            parentFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, frag)
                .addToBackStack(null)
                .commit()
        }
    }

    /* ================= TRENDING LIST ================= */

    private fun setupTrendingList() {
        trendingAdapter = CommunityAdapter(
            mutableListOf(),
            { post, position -> handleLike(post, position) },
            { post ->
                val intent = Intent(requireContext(), ReplyActivity::class.java)
                intent.putExtra("post_id", post.id)
                intent.putExtra("post_content", post.content)
                startActivity(intent)
            }
        )

        rvTrending.layoutManager = LinearLayoutManager(requireContext())
        rvTrending.adapter = trendingAdapter
    }


    /* ================= LOAD POSTS ================= */

    private fun loadTrendingPosts() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                allPosts = DataRepository.getPosts()

                val trendingPosts = allPosts.filter {
                    it.isTrending && !it.isHidden
                }

                trendingAdapter.updateData(trendingPosts.toMutableList())

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /* ================= LIKE HANDLER ================= */

    private fun handleLike(post: Post, position: Int) {
        // Optimistic UI
        post.isLiked = !post.isLiked
        post.likeCount += if (post.isLiked) 1 else -1
        trendingAdapter.notifyItemChanged(position)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val res = DataRepository.toggleLike(post.id)
                post.isLiked = res.isLiked
                post.likeCount = res.like_count
                trendingAdapter.notifyItemChanged(position)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /* ================= OPTIONAL LOCAL FILTER ================= */

    fun reloadData(category: String) {
        if (!this::trendingAdapter.isInitialized) return

        val filteredPosts = allPosts.filter {
            !it.isHidden && (category == "talks" || it.category == category)
        }

        trendingAdapter.updateData(filteredPosts.toMutableList())
    }
}
