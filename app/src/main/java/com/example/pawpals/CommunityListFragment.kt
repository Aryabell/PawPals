package com.example.pawpals

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CommunityListFragment : Fragment(R.layout.fragment_community_list) {

    // pasangan (title, id)
    private val categories = listOf(
        Pair("PawPals: Kesehatan", "health"),
        Pair("PawPals: Playdate", "playdate"),
        Pair("PawPals: Talks", "talks"),
        Pair("PawPals: Rekomendasi Barang", "reco")
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvCategories = view.findViewById<RecyclerView>(R.id.rvCategories)
        val rvTrending = view.findViewById<RecyclerView>(R.id.rvTrendingPosts)

        // -- safe hide header jika ada (lookup dinamis mencegah unresolved reference)
        val headerId = resources.getIdentifier("tvForumMainTitle", "id", requireContext().packageName)
        if (headerId != 0) {
            view.findViewById<View>(headerId)?.visibility = View.GONE
        }

        // horizontal categories
        rvCategories.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        // adapter category expects list of titles (String)
        val titles = categories.map { it.first }
        rvCategories.adapter = CategoryAdapter(titles) { selectedTitle ->
            // cari id dari title yang dipilih
            val pair = categories.find { it.first == selectedTitle }
            val id = pair?.second ?: "talks"

            // langsung pakai CommunityPostsFragment agar tidak double fragment
            val frag = CommunityPostsFragment.newInstance(selectedTitle, id)

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, frag)
                .addToBackStack(null)
                .commit()
        }

        // trending posts (sementara pakai DataRepository.posts)
        rvTrending.layoutManager = LinearLayoutManager(requireContext())
        rvTrending.adapter = CommunityAdapter(DataRepository.posts) { post ->
            val intent = Intent(requireContext(), ReplyActivity::class.java)
            intent.putExtra("post_id", post.id)
            intent.putExtra("post_title", post.author) // bold name
            intent.putExtra("post_content", post.content)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // setiap kali fragment Home muncul, kembalikan toolbar ke judul default dan matikan panah back
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = "PawPals Forum Community"
            setDisplayHomeAsUpEnabled(false)
        }
    }
}
