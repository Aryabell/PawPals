package com.example.pawpals

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : Fragment(R.layout.fragment_home) {
    // 1. Definisikan nilai elevasi yang diinginkan
    private val SCROLLED_ELEVATION_DP = 4f
    private val SCROLLED_ELEVATION_PX by lazy {
        resources.displayMetrics.density * SCROLLED_ELEVATION_DP
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ⭐️ Dapatkan NestedScrollView Anda
        val nestedScrollView: NestedScrollView = view.findViewById(R.id.home)
        // Ambil RecyclerView dari layout
        val rvPosts: RecyclerView = view.findViewById(R.id.rv_posts)

        // Buat data dummy
        val dummyPosts = getDummyPosts()

        // etup Adapter
        // Asumsi CommunityAdapter menerima List<Post> dan OnClickListener
        val postAdapter = CommunityAdapter(
            items = dummyPosts.toMutableList(), // Parameter pertama (List<Post>)
            onItemClick = { post ->               // <--- GANTI NAMA PARAMETER INI
                // TODO: Logika saat post diklik (misalnya, buka ReplyActivity)
            }
        )

        // 3. LOGIKA SCROLLING DAN ELEVASI BARU
        nestedScrollView.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->

                // Dapatkan Toolbar dari MainActivity
                val toolbar = (activity as? MainActivity)?.binding?.toolbar

                if (toolbar != null) {
                    // Jika scrollY > 0, artinya sudah digulir ke bawah, beri elevasi
                    if (scrollY > 0) {
                        if (toolbar.elevation != SCROLLED_ELEVATION_PX) {
                            toolbar.elevation = SCROLLED_ELEVATION_PX
                        }
                    } else {
                        // Jika scrollY == 0 (di paling atas), hilangkan elevasi
                        if (toolbar.elevation != 0f) {
                            toolbar.elevation = 0f
                        }
                    }
                }
            }
        )

        // 4. Pastikan elevasi awal di-set 0 saat fragment dimuat pertama kali
        (activity as? MainActivity)?.binding?.toolbar?.elevation = 0f

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

        // ⭐️ Pastikan elevasi kembali ke 0 saat fragment muncul lagi ⭐️
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
