package com.example.pawpals.event

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pawpals.data.Event
import com.example.pawpals.databinding.ItemEventBinding
import com.bumptech.glide.Glide
import com.example.pawpals.R
import com.bumptech.glide.load.engine.DiskCacheStrategy

class EventAdapter(
    private var items: List<Event>,
    private val onJoinClick: (Event) -> Unit,
    private val onItemClick: (Event) -> Unit
) : RecyclerView.Adapter<EventAdapter.VH>() {

    inner class VH(private val b: ItemEventBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(e: Event) {
            b.tvTitle.text = e.title
            b.tvDate.text = e.date
            b.tvLocation.text = e.location
            b.btnJoin.text = if (e.isJoined) "Joined" else "Join"
            b.btnJoin.isEnabled = !e.isJoined
            b.btnJoin.alpha = if (e.isJoined) 0.6f else 1f

            // Load gambar pake Glide
            Glide.with(b.root.context)
                .load(e.imageUrl)
                .placeholder(R.drawable.ic_placeholder) // tambahin drawable placeholder
                .error(R.drawable.ic_error) // tambahin gambar error biar ga blank kalau gagal
                .centerCrop()
                .thumbnail(0.25f) // ✅ load versi kecil dulu
                .diskCacheStrategy(DiskCacheStrategy.ALL) // ✅ cache di memori & disk
                .into(b.ivBanner)

            b.btnJoin.setOnClickListener { onJoinClick(e) }
            b.root.setOnClickListener { onItemClick(e) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newList: List<Event>) {
        items = newList
        notifyDataSetChanged()
    }
}
