package com.example.pawpals.community

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pawpals.R
import com.example.pawpals.data.DataRepository
import com.example.pawpals.model.Post
import kotlinx.coroutines.launch

class CommunityPostsFragment : Fragment(R.layout.fragment_posts) {

    companion object {
        private const val ARG_TITLE = "arg_title"
        private const val ARG_ID = "arg_id"

        fun newInstance(title: String, id: String): CommunityPostsFragment {
            return CommunityPostsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putString(ARG_ID, id)
                }
            }
        }
    }

    private lateinit var rv: RecyclerView
    private lateinit var tvTitle: TextView
    private lateinit var adapter: CommunityAdapter

    private var categoryId: String = ""
    private var categoryTitle: String = ""
    private var allPosts: List<Post> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoryTitle = arguments?.getString(ARG_TITLE).orEmpty()
        categoryId = arguments?.getString(ARG_ID).orEmpty()

        tvTitle = view.findViewById(R.id.tvForumTitle)
        rv = view.findViewById(R.id.rvCommunity)

        tvTitle.visibility = View.GONE

        setupRecyclerView()
        loadPosts()

        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = categoryTitle
            setDisplayHomeAsUpEnabled(true)
        }

        setHasOptionsMenu(true)
    }

    /* ================= RECYCLER ================= */

    private fun setupRecyclerView() {
        adapter = CommunityAdapter(
            mutableListOf(),
            { post, position -> handleLike(post, position) },
            { post ->
                val intent = Intent(requireContext(), ReplyActivity::class.java)
                intent.putExtra("post_id", post.id)
                intent.putExtra("post_content", post.content)
                startActivity(intent)
            }
        )

        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter
    }


    /* ================= LOAD POSTS ================= */

    private fun loadPosts() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                allPosts = DataRepository.getPosts()

                val filteredPosts = allPosts.filter {
                    !it.isHidden && it.category.equals(categoryId, ignoreCase = true)
                }

                adapter.updateData(filteredPosts.toMutableList())

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
        adapter.notifyItemChanged(position)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val res = DataRepository.toggleLike(post.id)
                post.isLiked = res.isLiked
                post.likeCount = res.like_count
                adapter.notifyItemChanged(position)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /* ================= BACK BUTTON ================= */

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            requireActivity().onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = "Pals Communities"
            setDisplayHomeAsUpEnabled(false)
        }
    }
}
