package com.example.pawpals.community

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pawpals.MainActivity
import com.example.pawpals.R
import com.example.pawpals.data.DataRepository
import com.example.pawpals.model.Post
import kotlinx.coroutines.launch

class CommunityFragment : Fragment(R.layout.fragment_community) {

    private val SCROLLED_ELEVATION_DP = 4f
    private val SCROLLED_ELEVATION_PX by lazy {
        resources.displayMetrics.density * SCROLLED_ELEVATION_DP
    }

    private lateinit var rvCommunities: RecyclerView
    private lateinit var rvTrending: RecyclerView
    private lateinit var tvTitle: TextView
    private lateinit var fabNew: View
    private lateinit var etSearch: EditText
    private lateinit var searchContainer: LinearLayout
    private lateinit var btnSearchCommunityToggle: ImageView

    private lateinit var trendingAdapter: TrendingAdapter
    private lateinit var createPostLauncher: ActivityResultLauncher<Intent>

    private var selectedCategoryId: String? = null
    private var allPostsCache: List<Post> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createPostLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    fetchPostsFromApi()
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nestedScrollView = view.findViewById<NestedScrollView>(R.id.community_scroll_view)
        val toolbar = (activity as? MainActivity)?.binding?.toolbar

        tvTitle = view.findViewById(R.id.tvForumTitle)
        rvCommunities = view.findViewById(R.id.rvCommunities)
        rvTrending = view.findViewById(R.id.rvTrending)
        fabNew = view.findViewById(R.id.fabNewPost)
        etSearch = view.findViewById(R.id.etSearchCommunity)
        searchContainer = view.findViewById(R.id.searchBarContainer)
        btnSearchCommunityToggle = view.findViewById(R.id.btnSearchCommunityToggle)

        // ================= TRENDING RV =================
        rvTrending.layoutManager = LinearLayoutManager(requireContext())
        trendingAdapter = TrendingAdapter(listOf()) { post ->
            val intent = Intent(requireContext(), ReplyActivity::class.java)
            intent.putExtra("post_id", post.id)
            intent.putExtra("post_content", post.content)
            startActivity(intent)
        }
        rvTrending.adapter = trendingAdapter

        // ================= CATEGORY RV =================
        val communities = listOf(
            CommunityCategory("health", "Kesehatan"),
            CommunityCategory("talks", "Talks"),
            CommunityCategory("playdate", "Playdate"),
            CommunityCategory("recommend", "Recommend")
        )

        rvCommunities.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvCommunities.adapter = CommunityListAdapter(communities) { category ->
            selectedCategoryId = category.id
            applyTrendingFilter()
        }

        // ================= SEARCH =================
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                trendingAdapter.filterData(s.toString())
            }
        })

        // ================= SEARCH TOGGLE =================
        btnSearchCommunityToggle.setOnClickListener {
            if (searchContainer.visibility == View.GONE) {
                searchContainer.visibility = View.VISIBLE
                tvTitle.visibility = View.GONE
                etSearch.requestFocus()
            } else {
                searchContainer.visibility = View.GONE
                tvTitle.visibility = View.VISIBLE
                etSearch.text.clear()
                applyTrendingFilter()
            }
        }

        // ================= NEW POST =================
        fabNew.setOnClickListener {
            val intent = Intent(requireContext(), NewPostActivity::class.java)
            intent.putExtra("category", selectedCategoryId ?: "talks")
            createPostLauncher.launch(intent)
        }

        // ================= TOOLBAR ELEVATION =================
        if (nestedScrollView != null && toolbar != null) {
            toolbar.elevation = 0f
            nestedScrollView.setOnScrollChangeListener(
                NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
                    toolbar.elevation =
                        if (scrollY > 0) SCROLLED_ELEVATION_PX else 0f
                }
            )
        }

        // ================= LOAD DATA =================
        fetchPostsFromApi()
    }

    private fun fetchPostsFromApi() {
        lifecycleScope.launch {
            allPostsCache = DataRepository.getPosts()
            applyTrendingFilter()
        }
    }

    private fun applyTrendingFilter() {
        val filtered = if (selectedCategoryId != null) {
            allPostsCache.filter {
                it.isTrending &&
                        !it.isHidden &&
                        it.category.equals(selectedCategoryId, ignoreCase = true)
            }
        } else {
            allPostsCache.filter {
                it.isTrending && !it.isHidden
            }
        }
        trendingAdapter.updateData(filtered)
    }


    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = "Pals Community"
            setDisplayHomeAsUpEnabled(false)
        }
        (activity as? MainActivity)?.binding?.toolbar?.elevation = 0f
    }
}
