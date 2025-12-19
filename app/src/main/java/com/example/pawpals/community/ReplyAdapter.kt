package com.example.pawpals.community

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pawpals.R
import com.example.pawpals.model.Reply
import java.text.SimpleDateFormat
import java.util.Locale

class ReplyAdapter(
    private val items: MutableList<Reply>
) : RecyclerView.Adapter<ReplyAdapter.ReplyVH>() {

    inner class ReplyVH(v: View) : RecyclerView.ViewHolder(v) {
        val tvAuthor: TextView = v.findViewById(R.id.tvReplyAuthor)
        val tvContent: TextView = v.findViewById(R.id.tvReplyContent)
        val tvTime: TextView = v.findViewById(R.id.tvReplyTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReplyVH {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.item_reply, parent, false)
        return ReplyVH(v)
    }

    override fun onBindViewHolder(holder: ReplyVH, position: Int) {
        val r = items[position]
        holder.tvAuthor.text = r.author
        holder.tvContent.text = r.content

        // ⬇️ FIX: createdAt dari API (String)
        holder.tvTime.text =
            getTimeAgo(parseReplyTimeToMillis(r.createdAt))
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newList: MutableList<Reply>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }
}

/* ===== TIME PARSER ===== */
fun parseReplyTimeToMillis(time: String): Long {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        sdf.parse(time)?.time ?: System.currentTimeMillis()
    } catch (e: Exception) {
        System.currentTimeMillis()
    }
}
