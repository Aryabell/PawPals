package com.example.pawpals

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {

    // com.example.pawpals/ui/HomeFragment.kt

    private fun getDummyPosts(): List<Post> {
        return listOf(
            Post(
                id = "p001",
                author = "Paw Admin", // Ganti dari username
                userRole = "Pengurus",
                category = "Lost Dogs", // Ganti dari communityTag
                timestamp = "7 mins ago", // Ganti dari timeAgo
                content = "Halo, Mipaw nemu lost doggi di deket esdisi", // Ganti dari contentText
                imageUri = "android.resource://com.example.pawpals/${R.drawable.ic_placeholder}", // Pake URL/Path placeholder
                commentCount = 12,
                likeCount = 45,
                userAvatar = R.drawable.ic_profile_placeholder
            ),
            Post(
                id = "p002",
                author = "OggyGooey",
                userRole = "Anggota",
                category = "Paw Playground",
                timestamp = "7 mins ago",
                content = "saran cafe buat main sama Oggy dong guys 🥺",
                imageUri = null, // Tidak ada gambar
                commentCount = 8,
                likeCount = 30,
                userAvatar = R.drawable.ic_profile_placeholder
            )
        )
    }
// Catatan: Untuk real imageUri, nanti perlu library seperti Glide/Picasso untuk load gambar dari String URL.
// Untuk dummy, saya pakai format String Path Resource.

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val btnLogout = view.findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            (activity as? MainActivity)?.logout()
        }

        return view
    }
}
