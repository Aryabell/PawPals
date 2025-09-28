package com.example.pawpals

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CommunityAdapter(
    private var items: MutableList<Post>,
    private val onItemClick: (Post) -> Unit
) : RecyclerView.Adapter<CommunityAdapter.PostVH>() {

    inner class PostVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProfile: ImageView = itemView.findViewById(R.id.imgProfile)
        val tvAuthor: TextView = itemView.findViewById(R.id.tvAuthor)
        val tvContent: TextView = itemView.findViewById(R.id.tvContent)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostVH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostVH(v)
    }

    override fun onBindViewHolder(holder: PostVH, position: Int) {
        val post = items[position]
        // placeholder image (ganti dengan image URL loader bila pakai)
        holder.imgProfile.setImageResource(R.drawable.ic_profile_placeholder)
        holder.tvAuthor.text = post.author
        holder.tvContent.text = post.content
        holder.tvTime.text = post.timestamp

        holder.itemView.setOnClickListener { onItemClick(post) }
    }

    override fun getItemCount(): Int = items.size

    // helper untuk update data
    fun updateData(newList: MutableList<Post>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }
}
