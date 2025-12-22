package com.example.pawpals.community

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pawpals.R
import com.example.pawpals.model.Post
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/* ================= HELPER ================= */

fun parseApiTimeToMillis(time: String): Long {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        sdf.parse(time)?.time ?: System.currentTimeMillis()
    } catch (e: Exception) {
        System.currentTimeMillis()
    }
}

fun getTimeAgo(timeMillis: Long): String {
    val diff = System.currentTimeMillis() - timeMillis
    return when {
        diff < TimeUnit.MINUTES.toMillis(1) -> "just now"
        diff < TimeUnit.HOURS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toMinutes(diff)} mins ago"
        diff < TimeUnit.DAYS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toHours(diff)}h ago"
        diff < TimeUnit.DAYS.toMillis(7) -> "${TimeUnit.MILLISECONDS.toDays(diff)}d ago"
        else -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(timeMillis))
    }
}

fun getTagColor(context: Context, tag: String): Int {
    return when (tag) {
        "Lost Dogs" -> ContextCompat.getColor(context, R.color.color_tag_lost_dogs)
        "Paw Playground" -> ContextCompat.getColor(context, R.color.color_tag_playground)

        "Adopsi", "Adoption" -> ContextCompat.getColor(context, R.color.color_tag_adoption)
        "Kesehatan", "Health" -> ContextCompat.getColor(context, R.color.color_tag_health)
        "Playdate" -> ContextCompat.getColor(context, R.color.color_tag_playdate)
        "Rekomendasi", "Recommend" -> ContextCompat.getColor(context, R.color.color_tag_recommend)
        "Events" -> ContextCompat.getColor(context, R.color.color_tag_events)
        "Talks" -> ContextCompat.getColor(context, R.color.color_tag_talks)

        else -> ContextCompat.getColor(context, android.R.color.darker_gray)
    }
}

fun isColorDark(color: Int): Boolean {
    val darkness = 1 - (0.299 * Color.red(color) +
            0.587 * Color.green(color) +
            0.114 * Color.blue(color)) / 255
    return darkness >= 0.5
}

/* ================= ADAPTER ================= */

class CommunityAdapter(
    private var items: MutableList<Post>,
    private val onLikeClick: (Post, Int) -> Unit,
    private val onPostClick: (Post) -> Unit   // ✅ TAMBAHAN
) : RecyclerView.Adapter<CommunityAdapter.PostVH>() {

    inner class PostVH(view: View) : RecyclerView.ViewHolder(view) {
        val imgProfile: ImageView = view.findViewById(R.id.imgProfile)
        val tvAuthor: TextView = view.findViewById(R.id.tvAuthor)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val tvRoleTag: TextView = view.findViewById(R.id.tv_community_tag)
        val tvContent: TextView = view.findViewById(R.id.tvContent)
        val imgPostImage: ImageView = view.findViewById(R.id.imgPostImage)
        val tvCommentCount: TextView = view.findViewById(R.id.tv_comment_count)
        val tvLikeCount: TextView = view.findViewById(R.id.tv_like_count)
        val ivLikeIcon: ImageView = view.findViewById(R.id.iv_like_icon)
        val likeContainer: View = view.findViewById(R.id.ll_like_action)
        val moreBtn: View = view.findViewById(R.id.moreBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return PostVH(view)
    }

    override fun onBindViewHolder(holder: PostVH, position: Int) {
        val post = items[position]
        val context = holder.itemView.context

        /* ===== TAG ===== */
        val color = getTagColor(context, post.category)
        val bg = DrawableCompat.wrap(holder.tvRoleTag.background).mutate()
        DrawableCompat.setTint(bg, color)
        holder.tvRoleTag.background = bg
        holder.tvRoleTag.setTextColor(
            if (isColorDark(color)) ContextCompat.getColor(context, R.color.white)
            else ContextCompat.getColor(context, R.color.text_dark)
        )

        /* ===== BASIC ===== */
        holder.imgProfile.setImageResource(R.drawable.ava_paw)
        holder.tvAuthor.text = post.author
        holder.tvRoleTag.text = post.category
        holder.tvContent.text = post.content
        holder.tvTime.text = getTimeAgo(parseApiTimeToMillis(post.created_at))

        /* ===== IMAGE ===== */
        if (!post.imageUri.isNullOrEmpty()) {
            holder.imgPostImage.visibility = View.VISIBLE // Langsung di ImageView
            Glide.with(context)
                .load("http://10.0.2.2/pawpals_api/${post.imageUri}")
                .into(holder.imgPostImage)
        } else {
            holder.imgPostImage.visibility = View.GONE // Langsung di ImageView
        }

        /* ===== COUNTS ===== */
        holder.tvCommentCount.text = post.commentCount.toString()
        holder.tvLikeCount.text = post.likeCount.toString()

        /* ===== LIKE UI ===== */
        holder.ivLikeIcon.setImageResource(
            if (post.isLiked) R.drawable.ic_favorite_filled
            else R.drawable.ic_favorite
        )

        holder.likeContainer.setOnClickListener {
            onLikeClick(post, holder.adapterPosition)
        }

        /* ===== CLICK POST → REPLY ===== */
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ReplyActivity::class.java)
            intent.putExtra("post_id", post.id.toString())
            intent.putExtra("author", post.author)
            intent.putExtra("content", post.content)
            intent.putExtra("community_tag", post.category)
            intent.putExtra("time", getTimeAgo(parseApiTimeToMillis(post.created_at)))
            intent.putExtra("like_count", post.likeCount)
            intent.putExtra("comment_count", post.commentCount)
            intent.putExtra("is_liked", post.isLiked)
            holder.itemView.context.startActivity(intent)
        }


        /* ===== MORE ===== */
        holder.moreBtn.setOnClickListener { view ->
            val popup = PopupMenu(view.context, view)
            popup.menuInflater.inflate(R.menu.menu_post_more, popup.menu)
            popup.show()
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newList: MutableList<Post>) {
        items = newList
        notifyDataSetChanged()
    }
}
