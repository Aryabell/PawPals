package com.example.pawpals.community

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import android.graphics.Color
import com.example.pawpals.R
import java.util.concurrent.TimeUnit
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import com.example.pawpals.model.Post

fun getTimeAgo(timeMillis: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timeMillis

    if (diff < 0) return "just now"

    return when {
        diff < TimeUnit.MINUTES.toMillis(1) -> "just now"
        diff < TimeUnit.HOURS.toMillis(1) ->
            "${TimeUnit.MILLISECONDS.toMinutes(diff)} mins ago"
        diff < TimeUnit.DAYS.toMillis(1) ->
            "${TimeUnit.MILLISECONDS.toHours(diff)}h ago"
        diff < TimeUnit.DAYS.toMillis(7) ->
            "${TimeUnit.MILLISECONDS.toDays(diff)}d ago"
        else -> {
            val formatter = SimpleDateFormat("MMM dd", Locale.getDefault())
            formatter.format(Date(timeMillis))
        }
    }
}

fun getTagColor(context: Context, tag: String): Int {
    return when (tag) {
        "Lost Dogs" -> ContextCompat.getColor(context, R.color.color_tag_lost_dogs)
        "Paw Playground" -> ContextCompat.getColor(context, R.color.color_tag_playground)
        "Adoption" -> ContextCompat.getColor(context, R.color.color_tag_adoption)
        "Health" -> ContextCompat.getColor(context, R.color.color_tag_health)
        "Playdate" -> ContextCompat.getColor(context, R.color.color_tag_playdate)
        "Recommend" -> ContextCompat.getColor(context, R.color.color_tag_recommend)
        "Events" -> ContextCompat.getColor(context, R.color.color_tag_events)
        "Talks" -> ContextCompat.getColor(context, R.color.color_tag_talks)
        else -> ContextCompat.getColor(context, android.R.color.darker_gray)
    }
}

fun isColorDark(color: Int): Boolean {
    val darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
    return darkness >= 0.5
}

class CommunityAdapter(
    private var items: MutableList<Post>,
    private val onItemClick: (Post) -> Unit
) : RecyclerView.Adapter<CommunityAdapter.PostVH>() {

    inner class PostVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProfile: ImageView = itemView.findViewById(R.id.imgProfile)
        val tvAuthor: TextView = itemView.findViewById(R.id.tvAuthor)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val tvRoleTag: TextView = itemView.findViewById(R.id.tv_community_tag)
        val tvContent: TextView = itemView.findViewById(R.id.tvContent)
        val cvImageContainer: View = itemView.findViewById(R.id.cv_post_image_container)
        val imgPostImage: ImageView = itemView.findViewById(R.id.imgPostImage)
        val tvCommentCount: TextView = itemView.findViewById(R.id.tv_comment_count)
        val tvLikeCount: TextView = itemView.findViewById(R.id.tv_like_count)
        val ivLikeIcon: ImageView = itemView.findViewById(R.id.iv_like_icon)
        val likeContainer: View = itemView.findViewById(R.id.ll_like_action)
        val moreBtn: View = itemView.findViewById(R.id.moreBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostVH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostVH(v)
    }

    override fun onBindViewHolder(holder: PostVH, position: Int) {
        val post = items[position]
        val context = holder.itemView.context


        val colorInt = getTagColor(context, post.category)
        val backgroundDrawable = holder.tvRoleTag.background
        if (backgroundDrawable != null) {
            val wrappedDrawable = DrawableCompat.wrap(backgroundDrawable).mutate()
            DrawableCompat.setTint(wrappedDrawable, colorInt)
            holder.tvRoleTag.background = wrappedDrawable
        } else {
            holder.tvRoleTag.setBackgroundColor(colorInt)
        }

        if (isColorDark(colorInt)) {
            holder.tvRoleTag.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            holder.tvRoleTag.setTextColor(ContextCompat.getColor(context, R.color.text_dark))
        }


        val drawableLeft = holder.tvRoleTag.compoundDrawablesRelative[0]
        if (drawableLeft != null) {
            val wrappedDrawable = DrawableCompat.wrap(drawableLeft).mutate()
            val iconTint = if (isColorDark(colorInt)) R.color.white else R.color.text_dark
            DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(context, iconTint))
            holder.tvRoleTag.setCompoundDrawablesRelativeWithIntrinsicBounds(
                wrappedDrawable,
                null, null, null
            )
        }


        holder.imgProfile.setImageResource(R.drawable.ava_paw)
        holder.tvAuthor.text = post.author
        holder.tvTime.text = getTimeAgo(post.timestamp)
        holder.tvRoleTag.text = post.category
        holder.tvContent.text = post.content

        if (post.imageUri != null) {
            holder.cvImageContainer.visibility = View.VISIBLE
            val resourceName = post.imageUri.substringAfterLast("/")
            val resourceId =
                context.resources.getIdentifier(resourceName, "drawable", context.packageName)
            if (resourceId != 0) {
                holder.imgPostImage.setImageResource(resourceId)
            } else {
                holder.cvImageContainer.visibility = View.GONE
            }
        } else {
            holder.cvImageContainer.visibility = View.GONE
        }

        holder.tvCommentCount.text = post.commentCount.toString()
        holder.tvLikeCount.text = post.likeCount.toString()


        holder.ivLikeIcon.setImageResource(
            if (post.isLiked) R.drawable.ic_favoritered else R.drawable.ic_favorite
        )

        holder.likeContainer.setOnClickListener {
            post.isLiked = !post.isLiked

            if (post.isLiked) {
                post.likeCount += 1
                holder.ivLikeIcon.setImageResource(R.drawable.ic_favoritered)
            } else {
                post.likeCount -= 1
                holder.ivLikeIcon.setImageResource(R.drawable.ic_favorite)
            }

            holder.tvLikeCount.text = post.likeCount.toString()
        }


        holder.itemView.setOnClickListener {
            val intent = Intent(context, ReplyActivity::class.java).apply {

                putExtra("post_id", post.id)
                putExtra("author", post.author)
                putExtra("content", post.content)
                putExtra("community_tag", post.category)
                putExtra("time", post.timestamp)
                putExtra("like_count", post.likeCount)
                putExtra("comment_count", post.commentCount)
                putExtra("is_liked", post.isLiked)
            }
            context.startActivity(intent)
        }


        holder.moreBtn.setOnClickListener { view ->
            val popup = PopupMenu(view.context, view)
            popup.menuInflater.inflate(R.menu.menu_post_more, popup.menu)

            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_report -> {
                        val dialog = ReportDialogFragment.newInstance(post.id)
                        (view.context as AppCompatActivity)
                            .supportFragmentManager
                            .let { dialog.show(it, "reportDialog") }
                        true
                    }

                    else -> false
                }
            }
            popup.show()
        }
    }


    override fun getItemCount(): Int = items.size

    fun updateData(newList: MutableList<Post>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }
}