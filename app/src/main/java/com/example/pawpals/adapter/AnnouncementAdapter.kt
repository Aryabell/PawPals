package com.example.pawpals.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pawpals.R

// Data Class sederhana buat Banner
data class Banner(val title: String, val subtitle: String, val colorRes: Int)

class AnnouncementAdapter(private val banners: List<Banner>) :
    RecyclerView.Adapter<AnnouncementAdapter.BannerVH>() {

    inner class BannerVH(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvBannerTitle)
        val tvSubtitle: TextView = view.findViewById(R.id.tvBannerSubtitle)
        val container: View = view.findViewById(R.id.bannerContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerVH {
        // Kita akan bikin layout item_banner.xml sebentar lagi
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_banner, parent, false)
        return BannerVH(view)
    }

    override fun onBindViewHolder(holder: BannerVH, position: Int) {
        val item = banners[position]
        holder.tvTitle.text = item.title
        holder.tvSubtitle.text = item.subtitle

        // Set warna background dinamis
        // Pastikan warna-warna ini ada di colors.xml atau pakai Color.parseColor
        holder.container.setBackgroundColor(holder.itemView.context.getColor(item.colorRes))
    }

    override fun getItemCount(): Int = banners.size
}