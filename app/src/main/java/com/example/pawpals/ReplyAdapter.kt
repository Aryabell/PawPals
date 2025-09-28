package com.example.pawpals

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReplyAdapter(
    private val items: MutableList<Reply>
) : RecyclerView.Adapter<ReplyAdapter.ReplyVH>() {

    inner class ReplyVH(v: View) : RecyclerView.ViewHolder(v) {
        val tvAuthor: TextView = v.findViewById(R.id.tvReplyAuthor)
        val tvContent: TextView = v.findViewById(R.id.tvReplyContent)
        val tvTime: TextView = v.findViewById(R.id.tvReplyTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReplyVH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_reply, parent, false)
        return ReplyVH(v)
    }

    override fun onBindViewHolder(holder: ReplyVH, position: Int) {
        val r = items[position]
        holder.tvAuthor.text = r.author
        holder.tvContent.text = r.content
        holder.tvTime.text = r.timestamp
    }

    override fun getItemCount(): Int = items.size
}
