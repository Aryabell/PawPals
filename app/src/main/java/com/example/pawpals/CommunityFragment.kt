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

    private val SCROLLED_ELEVATION_DP = 4f
    private val SCROLLED_ELEVATION_PX by lazy {
        resources.displayMetrics.density * SCROLLED_ELEVATION_DP
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = "Pals Communities"
            setDisplayHomeAsUpEnabled(false)
        }
        (activity as? MainActivity)?.binding?.toolbar?.elevation = 0f
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

        val nestedScrollView = view.findViewById<androidx.core.widget.NestedScrollView>(R.id.community_scroll_view)
        val mainActivity = activity as? MainActivity
        val toolbar = mainActivity?.binding?.toolbar

        tvTitle = view.findViewById(R.id.tvForumTitle)
        rvCommunities = view.findViewById(R.id.rvCommunities)
        rvTrending = view.findViewById(R.id.rvTrending)
        fabNew = view.findViewById(R.id.fabNewPost)

        // List komunitas horizontal
        val communities = listOf(
            CommunityCategory("health", "Health"),
            CommunityCategory("talks", "Talks"),
            CommunityCategory("playdate", "Playdate"),
            CommunityCategory("reco", "Recommend")
        )

        if (nestedScrollView != null && toolbar != null) {
            // 1. Set elevasi awal ke 0
            toolbar.elevation = 0f

            // 2. Tambahkan listener scroll
            nestedScrollView.setOnScrollChangeListener(
                androidx.core.widget.NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
                    if (scrollY > 0) {
                        // Munculkan shadow (elevasi) saat digulir ke bawah
                        if (toolbar.elevation != SCROLLED_ELEVATION_PX) {
                            toolbar.elevation = SCROLLED_ELEVATION_PX
                        }
                    } else {
                        // Hilangkan shadow (elevasi) saat berada di puncak
                        if (toolbar.elevation != 0f) {
                            toolbar.elevation = 0f
                        }
                    }
                }
            )
        }

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
            // Asumsi: Kirim categoryId yang sedang aktif jika ada, default ke 'Talks'
            val currentCategory = (rvCommunities.adapter as? CommunityListAdapter)?.getCurrentSelectedCategory()?.id ?: "Talks"
            intent.putExtra("category", currentCategory)
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
