package com.example.pawpals.admin.fragments

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.pawpals.R
import com.example.pawpals.data.DataRepository

class PostFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_post, container, false)
        val layout = v.findViewById<LinearLayout>(R.id.layoutPosts)
        layout.removeAllViews()

        val posts = DataRepository.posts

        if (posts.isEmpty()) {
            val tv = TextView(requireContext())
            tv.text = "Belum ada postingan."
            layout.addView(tv)
            return v
        }

        for (post in posts) {
            val item = inflater.inflate(R.layout.item_admin_post, layout, false)
            val txtContent = item.findViewById<TextView>(R.id.txtContent)
            val txtAuthor = item.findViewById<TextView>(R.id.txtAuthor)
            val btnMark = item.findViewById<Button>(R.id.btnMark)
            val btnDelete = item.findViewById<Button>(R.id.btnDelete)

            txtAuthor.text = "👤 ${post.author} • ${post.category}"
            txtContent.text = post.content

            if (post.isTrending) {
                btnMark.text = "⭐ Trending"
            } else {
                btnMark.text = "☆ Jadikan Trending"
            }

            if (post.isHidden) {
                txtContent.text = "(disembunyikan)"
                btnDelete.isEnabled = false
            }

            btnMark.setOnClickListener {
                val isNowTrending = DataRepository.toggleTrending(post.id)

                if (isNowTrending) {
                    btnMark.text = "⭐ Trending"
                    Toast.makeText(requireContext(), "Post ditandai sebagai trending ⭐", Toast.LENGTH_SHORT).show()
                } else {
                    btnMark.text = "☆ Not Trending"
                    Toast.makeText(requireContext(), "Post tidak lagi trending", Toast.LENGTH_SHORT).show()
                }
            }


            btnDelete.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("Sembunyikan Postingan")
                    .setMessage("Yakin ingin menyembunyikan postingan ini?")
                    .setPositiveButton("Ya") { _, _ ->
                        DataRepository.hidePost(post.id)
                        txtContent.text = "(disembunyikan)"
                        btnDelete.isEnabled = false
                        Toast.makeText(requireContext(), "Postingan disembunyikan ❌", Toast.LENGTH_SHORT).show()

                        // 🧩 Tambahan: refresh agar hilang dari user CommunityFragment
                        requireActivity().recreate()
                    }
                    .setNegativeButton("Batal", null)
                    .show()
            }

            layout.addView(item)
        }

        return v
    }
}
