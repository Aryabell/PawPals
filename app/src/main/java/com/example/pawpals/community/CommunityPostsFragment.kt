package com.example.pawpals.community

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pawpals.data.DataRepository
import com.example.pawpals.R

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


        tvTitle.visibility = View.GONE

        rv.layoutManager = LinearLayoutManager(requireContext())
        adapter = CommunityAdapter(mutableListOf()) { }
        rv.adapter = adapter

        loadPosts()

        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = categoryTitle
            setDisplayHomeAsUpEnabled(true)
        }


        setHasOptionsMenu(true)
    }

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

    private fun loadPosts() {
        val posts = DataRepository.getPostsByCategory(categoryId)
        adapter.updateData(posts.toMutableList())
    }
}
