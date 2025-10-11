package com.example.pawpals

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : Fragment(R.layout.fragment_home) {
    private val SCROLLED_ELEVATION_DP = 4f
    private val SCROLLED_ELEVATION_PX by lazy {
        resources.displayMetrics.density * SCROLLED_ELEVATION_DP
    }

    private lateinit var rvPosts: RecyclerView
    private lateinit var postAdapter: CommunityAdapter
    private lateinit var allPosts: MutableList<Post>
    private lateinit var etSearch: EditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nestedScrollView: NestedScrollView = view.findViewById(R.id.home)
        rvPosts = view.findViewById(R.id.rv_posts)
        etSearch = view.findViewById(R.id.et_search_home) // 🔍 ambil EditText dari layout

        val dummyPosts = getDummyPosts()
        allPosts = dummyPosts.toMutableList()

        postAdapter = CommunityAdapter(
            items = dummyPosts.toMutableList(),
            onItemClick = { post ->
                // TODO: Logika saat post diklik (misalnya buka ReplyActivity)
            }
        )

        rvPosts.layoutManager = LinearLayoutManager(context)
        rvPosts.adapter = postAdapter

        nestedScrollView.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
                val toolbar = (activity as? MainActivity)?.binding?.toolbar
                if (toolbar != null) {
                    toolbar.elevation = if (scrollY > 0) SCROLLED_ELEVATION_PX else 0f
                }
            }
        )

        (activity as? MainActivity)?.binding?.toolbar?.elevation = 0f

        // 🔍 Tambahan: fitur filter realtime saat user mengetik
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterPosts(s.toString())
            }
        })
    }

    private fun filterPosts(query: String?) {
        val filteredList = if (query.isNullOrBlank()) {
            allPosts
        } else {
            allPosts.filter {
                it.content.contains(query, ignoreCase = true) ||
                        it.author.contains(query, ignoreCase = true) ||
                        it.category.contains(query, ignoreCase = true)
            }.toMutableList()
        }
        rvPosts.adapter = CommunityAdapter(filteredList) { post ->
            // TODO: Aksi kalau post diklik
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.title = "PawPals!"
        (activity as? MainActivity)?.binding?.toolbar?.elevation = 0f
    }

    private fun getDummyPosts(): List<Post> {
        return listOf(
            Post(
                id = "p001",
                author = "Paw Admin",
                userRole = "Pengurus",
                category = "Lost Dogs",
                timestamp = "7 mins ago",
                content = "Halo, Mipaw nemu lost doggi di deket esdisi",
                imageUri = "image_1",
                commentCount = 12,
                likeCount = 45,
                userAvatar = R.drawable.ava_paw
            ),
            Post(
                id = "p002",
                author = "OggyGooey",
                userRole = "Anggota",
                category = "Paw Playground",
                timestamp = "10s ago",
                content = "saran cafe buat main sama Oggy dong guys 🥺",
                imageUri = null,
                commentCount = 8,
                likeCount = 30,
                userAvatar = R.drawable.image2
            )
        )
    }
}
