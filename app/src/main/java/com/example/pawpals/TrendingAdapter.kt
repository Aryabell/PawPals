package com.example.pawpals

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TrendingAdapter(
    private var items: List<Post>,
    private val onClick: (Post) -> Unit
) : RecyclerView.Adapter<TrendingAdapter.TrendingViewHolder>() {

    inner class TrendingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAuthor: TextView = itemView.findViewById(R.id.tvAuthor)
        val tvContent: TextView = itemView.findViewById(R.id.tvContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_trending_post, parent, false)
        return TrendingViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrendingViewHolder, position: Int) {
        val post = items[position]
        holder.tvAuthor.text = post.author
        holder.tvContent.text = post.content
        holder.itemView.setOnClickListener { onClick(post) }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<Post>) {
        items = newItems
        notifyDataSetChanged()
    }
}

