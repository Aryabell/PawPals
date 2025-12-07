package com.example.pawpals.community

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pawpals.R

// ganti nama biar ga tabrakan
data class CommunityCategory(val id: String, val title: String)

class CommunityListAdapter(
    private val items: List<CommunityCategory>,
    private val onClick: (CommunityCategory) -> Unit
) : RecyclerView.Adapter<CommunityListAdapter.CatViewHolder>() {


    private var selectedCategory: CommunityCategory? = null

    inner class CatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.imgCommunity)
        val tv: TextView = itemView.findViewById(R.id.tvCommunityName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_community, parent, false)
        return CatViewHolder(view)
    }

    override fun onBindViewHolder(holder: CatViewHolder, position: Int) {
        val category = items[position]
        holder.tv.text = category.title

        // Tentukan Drawable yang Benar
        val drawableRes = when (category.id.lowercase()) {
            "health" -> R.drawable.profilepicture
            "talks" -> R.drawable.profilepicture
            "playdate" -> R.drawable.profilepicture
            "recommend" -> R.drawable.profilepicture
            else -> R.drawable.ic_placeholder
        }

        holder.img.setImageResource(drawableRes)

        holder.itemView.setOnClickListener {
            selectedCategory = category
            onClick(category)
        }
    }

    override fun getItemCount(): Int = items.size

    fun getCurrentSelectedCategory(): CommunityCategory? {
        return selectedCategory
    }
}
