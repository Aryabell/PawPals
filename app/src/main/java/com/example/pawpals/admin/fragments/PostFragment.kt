package com.example.pawpals.admin.fragments

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.pawpals.R
import com.example.pawpals.data.DataRepository
import com.example.pawpals.model.Post

class PostFragment : Fragment() {

    private lateinit var layout: LinearLayout
    private lateinit var inflaterRef: LayoutInflater

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.fragment_post, container, false)
        layout = v.findViewById(R.id.layoutPosts)
        inflaterRef = inflater

        DataRepository.posts.observe(viewLifecycleOwner) { postList ->
            refreshPosts(postList)
        }

        return v
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

        for (post in posts) {
            val item = inflaterRef.inflate(R.layout.item_admin_post, layout, false)

            val txtContent = item.findViewById<TextView>(R.id.txtContent)
            val txtAuthor = item.findViewById<TextView>(R.id.txtAuthor)
            val btnMark = item.findViewById<Button>(R.id.btnMark)
            val btnDelete = item.findViewById<Button>(R.id.btnDelete)

            txtAuthor.text = "👤 ${post.author} • ${post.category}"
            txtContent.text = if (post.isHidden) "(disembunyikan)" else post.content

            btnMark.text = if (post.isTrending) "⭐ Trending" else "☆ Jadikan Trending"
            btnMark.setOnClickListener {
                val isNowTrending = DataRepository.toggleTrending(post.id)
                btnMark.text = if (isNowTrending) "⭐ Trending" else "☆ Jadikan Trending"
                Toast.makeText(
                    requireContext(),
                    if (isNowTrending)
                        "Post ditandai sebagai trending ⭐"
                    else
                        "Post tidak lagi trending",
                    Toast.LENGTH_SHORT
                ).show()
            }

            if (post.isHidden) {
                btnDelete.text = "Tampilkan"
                btnDelete.setOnClickListener {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Tampilkan Postingan")
                        .setMessage("Ingin menampilkan kembali postingan ini?")
                        .setPositiveButton("Ya") { _, _ ->
                            DataRepository.unhidePost(post.id)
                            Toast.makeText(requireContext(), "Postingan ditampilkan kembali ✅", Toast.LENGTH_SHORT).show()
                        }
                        .setNegativeButton("Batal", null)
                        .show()
                }
            } else {
                btnDelete.text = "Sembunyikan"
                btnDelete.setOnClickListener {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Sembunyikan Postingan")
                        .setMessage("Yakin ingin menyembunyikan postingan ini?")
                        .setPositiveButton("Ya") { _, _ ->
                            DataRepository.hidePost(post.id)
                            Toast.makeText(requireContext(), "Postingan disembunyikan ❌", Toast.LENGTH_SHORT).show()
                        }
                        .setNegativeButton("Batal", null)
                        .show()
                }
            }

            layout.addView(item)
        }
    }
}
