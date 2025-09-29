package com.example.pawpals

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
import java.util.concurrent.TimeUnit
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
            // Jika lebih dari seminggu, kembali ke format tanggal pendek
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
        else -> ContextCompat.getColor(context, android.R.color.darker_gray) // Default jika tag tidak dikenal
    }
}

// Fungsi untuk mengecek apakah warna itu terang atau gelap
fun isColorDark(color: Int): Boolean {
    // Menghitung luminance (kecerahan) warna menggunakan rumus standar
    val darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255

    // Ambang batas (0.5 adalah nilai umum, kamu bisa sesuaikan)
    return darkness >= 0.5
}

class CommunityAdapter(
    private var items: MutableList<Post>,
    private val onItemClick: (Post) -> Unit
) : RecyclerView.Adapter<CommunityAdapter.PostVH>() {

    inner class PostVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // 1. HEADER / PROFIL
        val imgProfile: ImageView = itemView.findViewById(R.id.imgProfile) // ID lama, pastikan ini ID avatar
        val tvAuthor: TextView = itemView.findViewById(R.id.tvAuthor)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)

        // TAMBAHAN BARU
        val tvRoleTag: TextView = itemView.findViewById(R.id.tv_community_tag) // TextView untuk Community/Role Tag (misal: Lost Dogs)

        // 2. KONTEN
        val tvContent: TextView = itemView.findViewById(R.id.tvContent)
        val cvImageContainer: View = itemView.findViewById(R.id.cv_post_image_container) // CardView/View pembungkus gambar
        val imgPostImage: ImageView = itemView.findViewById(R.id.imgPostImage) // ImageView Postingan

        // 3. ACTION
        val tvCommentCount: TextView = itemView.findViewById(R.id.tv_comment_count)
        val tvLikeCount: TextView = itemView.findViewById(R.id.tv_like_count)
        // val imgLikeButton: ImageView = itemView.findViewById(R.id.imgLikeButton) // Jika kamu punya button untuk like
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostVH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostVH(v)
    }

    override fun onBindViewHolder(holder: PostVH, position: Int) {
        val post = items[position]
        val context = holder.itemView.context

        // 1. Dapatkan warna berdasarkan kategori/tag
        val colorInt = getTagColor(context, post.category)

        // 2. Terapkan warna background (kode kamu sebelumnya)
        val backgroundDrawable = holder.tvRoleTag.background
        if (backgroundDrawable != null) {
            val wrappedDrawable = DrawableCompat.wrap(backgroundDrawable).mutate()
            DrawableCompat.setTint(wrappedDrawable, colorInt)
            holder.tvRoleTag.background = wrappedDrawable
        } else {
            holder.tvRoleTag.setBackgroundColor(colorInt)
        }

        // 3. LOGIKA WARNA FONT BARU
        if (isColorDark(colorInt)) {
            // Jika background gelap, gunakan teks putih
            holder.tvRoleTag.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            // Jika background terang, gunakan teks gelap
            holder.tvRoleTag.setTextColor(ContextCompat.getColor(context, R.color.text_dark))
        }

        // BIND TAG/ROLE
        holder.tvRoleTag.text = post.category

// 3. Ubah warna background
        if (backgroundDrawable != null) {
            // Memastikan drawable bisa diwarnai (tinted)
            val wrappedDrawable = DrawableCompat.wrap(backgroundDrawable).mutate()
            DrawableCompat.setTint(wrappedDrawable, colorInt)
            holder.tvRoleTag.background = wrappedDrawable
        } else {
            // Fallback jika tidak ada drawable
            holder.tvRoleTag.setBackgroundColor(colorInt)
        }

        // 4. LOGIKA WARNA IKON PAW
// Ikon Paw ada di index 0 (drawableStart)
        val drawableLeft = holder.tvRoleTag.compoundDrawablesRelative[0]

        if (drawableLeft != null) {
            // Ikon perlu di-mutate agar tinting tidak memengaruhi instance drawable lainnya
            val wrappedDrawable = DrawableCompat.wrap(drawableLeft).mutate()

            val iconTint = if (isColorDark(colorInt)) {
                R.color.white
            } else {
                R.color.text_dark // atau warna gelap lainnya
            }

            // Gunakan wrappedDrawable yang sudah di-mutate untuk set tint
            DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(context, iconTint))

            // Set drawable yang sudah di-tinting kembali ke TextView
            holder.tvRoleTag.setCompoundDrawablesRelativeWithIntrinsicBounds(
                wrappedDrawable, // drawableStart
                holder.tvRoleTag.compoundDrawablesRelative[1], // drawableTop
                holder.tvRoleTag.compoundDrawablesRelative[2], // drawableEnd
                holder.tvRoleTag.compoundDrawablesRelative[3]  // drawableBottom
            )
        }


        // 1. BIND DATA PROFILE & HEADER
        // [Avatar Tidak Muncul] Menggunakan userAvatar dari Post
        holder.imgProfile.setImageResource(post.userAvatar)

        holder.tvAuthor.text = post.author
        holder.tvTime.text = post.timestamp

        // BIND TAG/ROLE
        holder.tvRoleTag.text = post.category

        // 2. BIND KONTEN UTAMA
        // [OggyGooey] Konten berbeda akan muncul karena kita bind tvContent
        holder.tvContent.text = post.content

        // 3. BIND GAMBAR POSTINGAN
        if (post.imageUri != null) {
            holder.cvImageContainer.visibility = View.VISIBLE

            // Karena kita menggunakan String Path Resource di dummy (ic_placeholder, image_1, dll.)
            val resourceName = post.imageUri.substringAfterLast("/")
            val resourceId = context.resources.getIdentifier(resourceName, "drawable", context.packageName)

            if (resourceId != 0) {
                holder.imgPostImage.setImageResource(resourceId)
            } else {
                // Sembunyikan jika resource path tidak valid
                holder.cvImageContainer.visibility = View.GONE
            }
        } else {
            // Sembunyikan CardView gambar jika imageUri null (kasus OggyGooey)
            holder.cvImageContainer.visibility = View.GONE
        }

        // 4. BIND ACTION COUNT
        holder.tvCommentCount.text = post.commentCount.toString()
        holder.tvLikeCount.text = post.likeCount.toString()


        // Set Listener
        holder.itemView.setOnClickListener { onItemClick(post) }
    }

    override fun getItemCount(): Int = items.size

    // helper untuk update data
    fun updateData(newList: MutableList<Post>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }
}
