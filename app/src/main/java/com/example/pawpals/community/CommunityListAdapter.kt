package com.example.pawpals.community

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pawpals.R


data class CommunityCategory(val id: String, val title: String, val iconResId: Int)

class CommunityListAdapter(
    private val items: List<CommunityCategory>,
    private val onClick: (CommunityCategory) -> Unit
) : RecyclerView.Adapter<CommunityListAdapter.CatViewHolder>() {


    private var selectedCategory: CommunityCategory? = null

    inner class CatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.ivIcon)
        val tv: TextView = itemView.findViewById(R.id.tvCategoryName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CatViewHolder(view)
    }

    override fun onBindViewHolder(holder: CatViewHolder, position: Int) {
        val category = items[position]
        holder.tv.text = category.title
        holder.img.setImageResource(category.iconResId)

        val drawableRes = when (category.id.lowercase()) {
            "health" -> R.drawable.ic_community_health
            "talks" -> R.drawable.ic_community_talks
            "playdate" -> R.drawable.ic_community_playdate
            "recommend" -> R.drawable.ic_community_recommend
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
