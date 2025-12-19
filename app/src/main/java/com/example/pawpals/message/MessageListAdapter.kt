package com.example.pawpals.message

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.pawpals.R
import com.example.pawpals.databinding.ItemMessageBinding

class MessageListAdapter(
    private val chats: List<ChatPreview>,
    private val onClick: (ChatPreview) -> Unit
) : RecyclerView.Adapter<MessageListAdapter.VH>() {

    inner class VH(private val binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chat: ChatPreview) = with(binding) {

            tvUserName.text = chat.user_name
            tvLastMessage.text = chat.last_message ?: ""

            tvTime.text = chat.timestamp?.let {
                DateFormat.format("HH:mm", it)
            } ?: ""

            // sementara pakai placeholder (karena API belum kirim foto)
            imgProfile.load(R.drawable.ic_profile_placeholder)

            root.setOnClickListener {
                onClick(chat)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemMessageBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(chats[position])
    }

    override fun getItemCount(): Int = chats.size
}
