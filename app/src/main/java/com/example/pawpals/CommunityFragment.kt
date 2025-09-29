package com.example.pawpals

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.app.AppCompatActivity

class CommunityFragment : Fragment(R.layout.fragment_community) {

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = "Pals Communities"
            setDisplayHomeAsUpEnabled(false)
        }
    }

    private lateinit var rvCommunities: RecyclerView
    private lateinit var rvTrending: RecyclerView
    private lateinit var tvTitle: TextView
    private lateinit var fabNew: View

    private lateinit var trendingAdapter: TrendingAdapter
    private lateinit var createPostLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createPostLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    loadTrendingPosts() // reload saat balik dari NewPostActivity
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvTitle = view.findViewById(R.id.tvForumTitle)
        rvCommunities = view.findViewById(R.id.rvCommunities)
        rvTrending = view.findViewById(R.id.rvTrending)
        fabNew = view.findViewById(R.id.fabNewPost)

        // List komunitas horizontal
        val communities = listOf(
            CommunityCategory("health", "PawPals: Kesehatan"),
            CommunityCategory("talks", "PawPals: Talks"),
            CommunityCategory("playdate", "PawPals: Playdate"),
            CommunityCategory("reco", "PawPals: Rekomendasi Barang")
        )

        rvCommunities.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvCommunities.adapter = CommunityListAdapter(communities) { category ->
            // klik kategori → cukup update list trending saja
            loadTrendingPosts(category.id)
        }

        // Trending posts
        rvTrending.layoutManager = LinearLayoutManager(requireContext())
        trendingAdapter = TrendingAdapter(listOf()) { post ->
            val intent = Intent(requireContext(), ReplyActivity::class.java)
            intent.putExtra("post_id", post.id)
            intent.putExtra("post_content", post.content)
            startActivity(intent)
        }
        rvTrending.adapter = trendingAdapter

        // tampilkan trending awal
        loadTrendingPosts()

        fabNew.setOnClickListener {
            val intent = Intent(requireContext(), NewPostActivity::class.java)
            createPostLauncher.launch(intent)
        }
    }

    /** sekarang bisa dipanggil untuk semua kategori */
    private fun loadTrendingPosts(categoryId: String? = null) {
        val trending = if (categoryId != null) {
            // ambil post trending sesuai kategori
            DataRepository.getTrendingPostsByCategory(categoryId)
        } else {
            // default ambil semua post trending
            DataRepository.getTrendingPosts()
        }
        trendingAdapter.updateData(trending)
    }
}
