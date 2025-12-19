package com.example.pawpals.message

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.pawpals.R
import com.example.pawpals.databinding.ItemChatMessageBinding
import com.example.pawpals.message.ChatMessage

class ChatAdapter(
    private val messages: List<ChatMessage>,
    private val currentUserId: Int
) : RecyclerView.Adapter<ChatAdapter.VH>() {

    inner class VH(private val binding: ItemChatMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(msg: ChatMessage) {
            val isMine = msg.senderId == currentUserId
            binding.tvMessage.apply {
                text = msg.message
                background = if (isMine)
                    context.getDrawable(R.drawable.bg_bubble_me)
                else
                    context.getDrawable(R.drawable.bg_bubble_other)

                val layoutParams = layoutParams as FrameLayout.LayoutParams
                layoutParams.gravity = if (isMine) Gravity.END else Gravity.START
                this.layoutParams = layoutParams
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemChatMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(messages[position])
    override fun getItemCount(): Int = messages.size
}
