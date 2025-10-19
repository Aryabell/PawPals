package com.example.pawpals.message

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pawpals.databinding.ItemMessageBinding
import com.example.pawpals.message.ChatPreview
import com.example.pawpals.R
import coil.load

class MessageListAdapter(
    private val chats: List<ChatPreview>,
    private val onClick: (ChatPreview) -> Unit
) : RecyclerView.Adapter<MessageListAdapter.VH>() {

    inner class VH(val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: ChatPreview) {
            with(binding) {
                tvUserName.text = chat.userName
                tvLastMessage.text = chat.lastMessage
                tvTime.text = android.text.format.DateFormat.format("HH:mm", chat.timestamp)
                imgProfile.load(chat.userImageResId) {
                    placeholder(com.example.pawpals.R.drawable.ic_profile_placeholder)
                }
                root.setOnClickListener { onClick(chat) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(chats[position])
    override fun getItemCount(): Int = chats.size
}
