package com.example.pawpals.event

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.pawpals.R
import com.example.pawpals.data.Event
import com.example.pawpals.databinding.ItemEventBinding

class   EventAdapter(
    private var items: List<Event>,
    private val onJoinClick: (Event) -> Unit = {},
    private val onItemClick: (Event) -> Unit = {},
    private val onDeleteClick: ((Event) -> Unit)? = null,
    private val isAdmin: Boolean = false
) : RecyclerView.Adapter<EventAdapter.VH>() {

    inner class VH(private val b: ItemEventBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(e: Event) {
            b.tvTitle.text = e.title
            b.tvDate.text = e.date
            b.tvLocation.text = e.location

            // Gambar banner pakai Glide
            Glide.with(b.root.context)
                .load(e.imageUrl)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_error)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(b.ivBanner)

            if (isAdmin) {
                // Admin hanya bisa hapus event
                b.btnJoin.visibility = View.GONE
                b.btnDelete.visibility = View.VISIBLE
                b.btnDelete.setOnClickListener { onDeleteClick?.invoke(e) }
            } else {
                // Member hanya bisa join
                b.btnJoin.visibility = View.VISIBLE
                b.btnDelete.visibility = View.GONE

                // *********** LOGIKA TAMPILAN TOMBOL JOIN ***********
                if (e.isJoined) {
                    b.btnJoin.text = "Joined"
                    b.btnJoin.isEnabled = false
                    // Menggunakan warna yang lebih terang/abu-abu saat sudah joined
                    b.btnJoin.backgroundTintList = b.root.context.getColorStateList(R.color.gray_light)
                } else {
                    b.btnJoin.text = "Join"
                    b.btnJoin.isEnabled = true
                    // Menggunakan primary_blue saat belum joined
                    b.btnJoin.backgroundTintList = b.root.context.getColorStateList(R.color.primary_blue)
                }

                b.btnJoin.setOnClickListener { onJoinClick(e) }
            }

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

    override fun getItemCount() = items.size

    fun submitList(newList: List<Event>) {
        items = newList
        notifyDataSetChanged()
    }
}
