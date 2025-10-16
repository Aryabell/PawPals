package com.example.pawpals.community

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pawpals.R
import com.example.pawpals.data.DataRepository

class CommunityListFragment : Fragment(R.layout.fragment_community_list) {

    private lateinit var rvTrending: RecyclerView
    private lateinit var rvCategories: RecyclerView

    private val categories = listOf(
        "Health" to "health",
        "Playdate" to "playdate",
        "Talks" to "talks",
        "Recommend" to "reco"
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvCategories = view.findViewById(R.id.rvCategories)
        rvTrending = view.findViewById(R.id.rvTrendingPosts)

        val headerId = resources.getIdentifier("tvForumMainTitle", "id", requireContext().packageName)
        if (headerId != 0) {
            view.findViewById<View>(headerId)?.visibility = View.GONE
        }

        rvCategories.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val titles = categories.map { it.first }
        rvCategories.adapter = CategoryAdapter(titles) { selectedTitle ->
            val pair = categories.find { it.first == selectedTitle }
            val id = pair?.second ?: "talks"

            val frag = CommunityPostsFragment.newInstance(selectedTitle, id)

            parentFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, frag)
                .addToBackStack(null)
                .commit()
        }

        loadTrendingPosts()
    }

    private fun loadTrendingPosts() {
        val trendingPosts = DataRepository.posts.value
            ?.filter { it.isTrending && !it.isHidden }
            ?: emptyList()

        rvTrending.layoutManager = LinearLayoutManager(requireContext())
        rvTrending.adapter = CommunityAdapter(trendingPosts.toMutableList()) { post ->
            val intent = Intent(requireContext(), ReplyActivity::class.java).apply {
                putExtra("post_id", post.id)
                putExtra("post_title", post.author)
                putExtra("post_content", post.content)
            }
            startActivity(intent)
        }
    }

    fun reloadData(category: String) {
        if (!this::rvTrending.isInitialized) return

        val filteredPosts = DataRepository.posts.value
            ?.filter { !it.isHidden && (category == "talks" || it.category == category) }
            ?: emptyList()

        rvTrending.adapter = CommunityAdapter(filteredPosts.toMutableList()) { post ->
            val intent = Intent(requireContext(), ReplyActivity::class.java).apply {
                putExtra("post_id", post.id)
                putExtra("post_title", post.author)
                putExtra("post_content", post.content)
            }
            startActivity(intent)
        }
    }
}
