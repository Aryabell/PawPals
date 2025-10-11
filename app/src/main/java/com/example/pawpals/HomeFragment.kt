package com.example.pawpals

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : Fragment(R.layout.fragment_home) {
    // 1. Definisikan nilai elevasi yang diinginkan
    private val SCROLLED_ELEVATION_DP = 4f
    private val SCROLLED_ELEVATION_PX by lazy {
        resources.displayMetrics.density * SCROLLED_ELEVATION_DP
    }

    // ✨ Tambahan: variabel global biar bisa akses di fungsi lain
    private lateinit var rvPosts: RecyclerView
    private lateinit var postAdapter: CommunityAdapter
    private lateinit var allPosts: MutableList<Post>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Tambahin biar fragment ini bisa punya menu (toolbar)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ⭐️ Dapatkan NestedScrollView Anda
        val nestedScrollView: NestedScrollView = view.findViewById(R.id.home)
        // Ambil RecyclerView dari layout
        val rvPosts: RecyclerView = view.findViewById(R.id.rv_posts)

        // Buat data dummy
        val dummyPosts = getDummyPosts()

        // ✨ Simpan ke variabel global biar bisa difilter nanti
        allPosts = dummyPosts.toMutableList()

        // etup Adapter
        // Asumsi CommunityAdapter menerima List<Post> dan OnClickListener
        val postAdapter = CommunityAdapter(
            items = dummyPosts.toMutableList(), // Parameter pertama (List<Post>)
            onItemClick = { post ->               // <--- GANTI NAMA PARAMETER INI
                // TODO: Logika saat post diklik (misalnya, buka ReplyActivity)
            }
        )

        // ✨ Simpan juga adapter-nya ke variabel global
        this.rvPosts = rvPosts
        this.postAdapter = postAdapter

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

    // ✨ Tambahan baru: bikin menu search di toolbar
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_menu, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.queryHint = "Cari post, username, atau role..."

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterPosts(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterPosts(newText)
                return true
            }
        })
    }

    // ✨ Fungsi tambahan buat filter postingan
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
