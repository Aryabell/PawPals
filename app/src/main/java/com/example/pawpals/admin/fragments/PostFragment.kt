package com.example.pawpals.admin.fragments

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.pawpals.R
import com.example.pawpals.data.DataRepository
import com.example.pawpals.model.Post
import kotlinx.coroutines.launch

class PostFragment : Fragment() {

    private lateinit var layout: LinearLayout
    private lateinit var inflaterRef: LayoutInflater

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_post, container, false)

        layout = view.findViewById(R.id.layoutPosts)
        inflaterRef = inflater

        // Observe data dari repository (database)
        DataRepository.posts.observe(viewLifecycleOwner) { posts ->
            refreshPosts(posts)
        }

        // Load post pertama kali dari database
        lifecycleScope.launch {
            DataRepository.getPosts()
        }

        return view
    }

    private fun refreshPosts(posts: List<Post>?) {
        layout.removeAllViews()

        if (posts.isNullOrEmpty()) {
            val tv = TextView(requireContext()).apply {
                text = "Belum ada postingan."
                setPadding(16, 16, 16, 16)
            }
            layout.addView(tv)
            return
        }

        posts.forEach { post ->
            val item = inflaterRef.inflate(
                R.layout.item_admin_post,
                layout,
                false
            )

            val txtContent = item.findViewById<TextView>(R.id.txtContent)
            val txtAuthor = item.findViewById<TextView>(R.id.txtAuthor)
            val btnTrending = item.findViewById<Button>(R.id.btnMark)
            val btnHidden = item.findViewById<Button>(R.id.btnDelete)

            // Author + category
            txtAuthor.text = "👤 ${post.author} • ${post.category}"

            // Content
            txtContent.text =
                if (post.isHidden) "(disembunyikan)" else post.content

            /* ================= TRENDING ================= */

            btnTrending.text =
                if (post.isTrending) "⭐ Trending"
                else "☆ Jadikan Trending"

            btnTrending.setOnClickListener {
                lifecycleScope.launch {
                    DataRepository.toggleTrending(post.id)
                    Toast.makeText(
                        requireContext(),
                        if (!post.isTrending)
                            "Post ditandai sebagai trending ⭐"
                        else
                            "Post tidak lagi trending",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            /* ================= HIDE / UNHIDE ================= */

            if (post.isHidden) {
                btnHidden.text = "Tampilkan"
                btnHidden.setOnClickListener {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Tampilkan Postingan")
                        .setMessage("Ingin menampilkan kembali postingan ini?")
                        .setPositiveButton("Ya") { _, _ ->
                            lifecycleScope.launch {
                                DataRepository.unhidePost(post.id)
                                Toast.makeText(
                                    requireContext(),
                                    "Postingan ditampilkan kembali ✅",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        .setNegativeButton("Batal", null)
                        .show()
                }
            } else {
                btnHidden.text = "Sembunyikan"
                btnHidden.setOnClickListener {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Sembunyikan Postingan")
                        .setMessage("Yakin ingin menyembunyikan postingan ini?")
                        .setPositiveButton("Ya") { _, _ ->
                            lifecycleScope.launch {
                                DataRepository.hidePost(post.id)
                                Toast.makeText(
                                    requireContext(),
                                    "Postingan disembunyikan ❌",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        .setNegativeButton("Batal", null)
                        .show()
                }
            }

            layout.addView(item)
        }
    }
}
