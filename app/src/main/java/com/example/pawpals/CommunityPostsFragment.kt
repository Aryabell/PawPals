package com.example.pawpals

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CommunityPostsFragment : Fragment(R.layout.fragment_posts) {

    companion object {
        private const val ARG_TITLE = "arg_title"
        private const val ARG_ID = "arg_id"

        fun newInstance(title: String, id: String): CommunityPostsFragment {
            val f = CommunityPostsFragment()
            val b = Bundle()
            b.putString(ARG_TITLE, title)
            b.putString(ARG_ID, id)
            f.arguments = b
            return f
        }
    }

    private lateinit var rv: RecyclerView
    private lateinit var tvTitle: TextView
    private lateinit var adapter: CommunityAdapter
    private var categoryId: String = ""
    private var categoryTitle: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoryTitle = arguments?.getString(ARG_TITLE) ?: ""
        categoryId = arguments?.getString(ARG_ID) ?: ""

        tvTitle = view.findViewById(R.id.tvForumTitle)
        rv = view.findViewById(R.id.rvCommunity)

        // kita sembunyikan text title di layout fragment (biar tidak dobel)
        tvTitle.visibility = View.GONE

        rv.layoutManager = LinearLayoutManager(requireContext())
        adapter = CommunityAdapter(mutableListOf()) { post ->
            val intent = Intent(requireContext(), ReplyActivity::class.java)
            intent.putExtra("post_id", post.id)
            intent.putExtra("post_content", post.content)
            startActivity(intent)
        }
        rv.adapter = adapter

        loadPosts()

        // set judul & tombol back di Toolbar activity
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = categoryTitle
            setDisplayHomeAsUpEnabled(true)
        }

        // supaya fragment bisa handle panah back
        setHasOptionsMenu(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            // klik panah back di toolbar → kembali
            requireActivity().onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // begitu fragment hilang (balik ke home), kembalikan toolbar ke default
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = "Pals Communities"
            setDisplayHomeAsUpEnabled(false)
        }
    }

    private fun loadPosts() {
        val posts = DataRepository.getPostsByCategory(categoryId)
        adapter.updateData(posts.toMutableList())
    }
}
