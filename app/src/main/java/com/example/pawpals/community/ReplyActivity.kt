package com.example.pawpals.community

import androidx.core.graphics.drawable.DrawableCompat
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pawpals.data.DataRepository
import com.example.pawpals.R

class ReplyActivity : AppCompatActivity() {

    private lateinit var rvReplies: RecyclerView
    private lateinit var adapter: ReplyAdapter
    private lateinit var postId: String

    private var selectedImageUri: Uri? = null
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private var isPostLiked: Boolean = false
    private var currentLikeCount: Int = 0
    private lateinit var ivLikeIcon: ImageView
    private lateinit var tvLikeCount: TextView
    private lateinit var llLikeAction: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reply)


        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Balasan"

        postId = intent.getStringExtra("post_id") ?: ""

        val author = intent.getStringExtra("author") ?: "Unknown"
        val content = intent.getStringExtra("content") ?: ""
        val communityTag = intent.getStringExtra("community_tag") ?: "Umum"
        val time = intent.getStringExtra("time") ?: "Baru saja"
        currentLikeCount = intent.getIntExtra("like_count", 0)
        val commentCount = intent.getIntExtra("comment_count", 0)
        isPostLiked = intent.getBooleanExtra("is_liked", false)


        val tvAuthor = findViewById<TextView>(R.id.tvAuthor)
        val tvContentPost = findViewById<TextView>(R.id.tvContent)
        val tvCommunityTag = findViewById<TextView>(R.id.tv_community_tag)
        val tvTime = findViewById<TextView>(R.id.tvTime)


        tvLikeCount = findViewById(R.id.tv_like_count)
        val tvCommentCount = findViewById<TextView>(R.id.tv_comment_count)
        ivLikeIcon = findViewById(R.id.iv_like_icon)
        llLikeAction = findViewById(R.id.ll_like_action)


        rvReplies = findViewById(R.id.rvReplies)
        val etReply = findViewById<EditText>(R.id.etReply)
        val btnSend = findViewById<ImageButton>(R.id.btnSend)
        val btnPickReplyImage = findViewById<ImageButton>(R.id.btnPickReplyImage)
        val btnRemoveReplyImage = findViewById<ImageButton>(R.id.btnRemoveReplyImage)
        val imgReplyPreview = findViewById<ImageView>(R.id.imgReplyPreview)


        tvAuthor.text = author
        tvContentPost.text = content
        tvCommunityTag.text = communityTag
        tvTime.text = time
        tvLikeCount.text = currentLikeCount.toString()
        tvCommentCount.text = commentCount.toString()


        val colorInt = getTagColor(this, communityTag)
        val backgroundDrawable = tvCommunityTag.background

        if (backgroundDrawable != null) {
            val wrappedDrawable = DrawableCompat.wrap(backgroundDrawable).mutate()
            androidx.core.graphics.drawable.DrawableCompat.setTint(wrappedDrawable, colorInt)
            tvCommunityTag.background = wrappedDrawable
        } else {
            tvCommunityTag.setBackgroundColor(colorInt)
        }


        if (isColorDark(colorInt)) {
            tvCommunityTag.setTextColor(ContextCompat.getColor(this, R.color.white))
        } else {
            tvCommunityTag.setTextColor(ContextCompat.getColor(this, R.color.text_dark))
        }


        val drawableLeft = tvCommunityTag.compoundDrawablesRelative[0]
        if (drawableLeft != null) {
            val wrappedDrawable = androidx.core.graphics.drawable.DrawableCompat.wrap(drawableLeft).mutate()
            val iconTint = if (isColorDark(colorInt)) R.color.white else R.color.text_dark
            androidx.core.graphics.drawable.DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(this, iconTint))
            tvCommunityTag.setCompoundDrawablesRelativeWithIntrinsicBounds(
                wrappedDrawable,
                null, null, null
            )
        }


        updateLikeUI()


        llLikeAction.setOnClickListener {
            isPostLiked = !isPostLiked
            if (isPostLiked) {
                currentLikeCount += 1
            } else {
                currentLikeCount -= 1
            }
            tvLikeCount.text = currentLikeCount.toString()
            updateLikeUI()
        }
        rvReplies.layoutManager = LinearLayoutManager(this)
        val repliesList = DataRepository.getReplies(postId)
        adapter = ReplyAdapter(repliesList)
        rvReplies.adapter = adapter


        pickImageLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            if (uri != null) {
                selectedImageUri = uri
                imgReplyPreview.setImageURI(uri)
                imgReplyPreview.visibility = View.VISIBLE
                btnRemoveReplyImage.visibility = View.VISIBLE
            }
        }


        btnPickReplyImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }


        btnRemoveReplyImage.setOnClickListener {
            selectedImageUri = null
            imgReplyPreview.setImageDrawable(null)
            imgReplyPreview.visibility = View.GONE
            btnRemoveReplyImage.visibility = View.GONE
        }

        btnSend.setOnClickListener {
            val text = etReply.text.toString().trim()
            if (text.isEmpty() && selectedImageUri == null) {
                Toast.makeText(this, "Tulis teks atau pilih gambar dulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            DataRepository.addReply(
                postId = postId,
                author = "You",
                content = text,
                imageUri = selectedImageUri?.toString()
            )

            adapter.notifyItemInserted(DataRepository.getReplies(postId).lastIndex)
            rvReplies.scrollToPosition(DataRepository.getReplies(postId).lastIndex)


            etReply.setText("")
            selectedImageUri = null
            imgReplyPreview.setImageDrawable(null)
            imgReplyPreview.visibility = View.GONE
            btnRemoveReplyImage.visibility = View.GONE
        }
    }

    private fun updateLikeUI() {
        if (isPostLiked) {
            ivLikeIcon.setImageResource(R.drawable.ic_favoritered)
        } else {
            ivLikeIcon.setImageResource(R.drawable.ic_favorite)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finish()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}
