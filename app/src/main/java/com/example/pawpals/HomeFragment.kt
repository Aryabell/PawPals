package com.example.pawpals

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Ambil RecyclerView dari layout
        val rvPosts: RecyclerView = view.findViewById(R.id.rv_posts)

        // 2. Buat data dummy
        val dummyPosts = getDummyPosts()

        // 3. Setup Adapter
        // Asumsi CommunityAdapter menerima List<Post> dan OnClickListener
        val postAdapter = CommunityAdapter(
            items = dummyPosts.toMutableList(), // Parameter pertama (List<Post>)
            onItemClick = { post ->               // <--- GANTI NAMA PARAMETER INI
                // TODO: Logika saat post diklik (misalnya, buka ReplyActivity)
            }
        )

        // 4. Set LayoutManager dan Adapter
        rvPosts.layoutManager = LinearLayoutManager(context)
        rvPosts.adapter = postAdapter

        // **CATATAN:** Jika kamu masih punya Button Logout, masukkan ke sini
        // val btnLogout = view.findViewById<Button>(R.id.btnLogout)
        // btnLogout.setOnClickListener { (activity as? MainActivity)?.logout() }
    }


    override fun onResume() {
        super.onResume()
        // Pastikan judul di set ke "PawPals!" saat HomeFragment aktif
        (activity as AppCompatActivity).supportActionBar?.title = "PawPals!"
    }

    private fun getDummyPosts(): List<Post> {
        return listOf(
            Post(
                id = "p001",
                author = "Paw Admin", // Ganti dari username
                userRole = "Pengurus",
                category = "Lost Dogs", // Ganti dari communityTag
                timestamp = "7 mins ago", // Ganti dari timeAgo
                content = "Halo, Mipaw nemu lost doggi di deket esdisi", // Ganti dari contentText
                imageUri = "image_1", // Pake URL/Path placeholder
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
                imageUri = null, // Tidak ada gambar
                commentCount = 8,
                likeCount = 30,
                userAvatar = R.drawable.ic_profile_placeholder
            )
        )
    }

}
