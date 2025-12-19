package com.example.pawpals.community

import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pawpals.R
import com.example.pawpals.data.DataRepository
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class ReplyActivity : AppCompatActivity() {

    private lateinit var rvReplies: RecyclerView
    private lateinit var adapter: ReplyAdapter
    private lateinit var postId: String

    private var selectedImageUri: Uri? = null
    private lateinit var username: String
    private lateinit var role: String

    // ===== LIKE STATE =====
    private var isLiked = false
    private var likeCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reply)

        // ===== USER SESSION =====
        val prefs = getSharedPreferences("user_session", MODE_PRIVATE)
        username = prefs.getString("username", "Anon") ?: "Anon"
        role = prefs.getString("role", "user") ?: "user"

        // ===== TOOLBAR =====
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Balasan"

        // ===== INTENT DATA =====
        postId = intent.getStringExtra("post_id") ?: ""

        val author = intent.getStringExtra("author") ?: "Unknown"
        val content = intent.getStringExtra("content") ?: ""
        val communityTag = intent.getStringExtra("community_tag") ?: "Umum"
        val time = intent.getStringExtra("time") ?: ""

        likeCount = intent.getIntExtra("like_count", 0)
        isLiked = intent.getBooleanExtra("is_liked", false)
        val commentCount = intent.getIntExtra("comment_count", 0)

        // ===== HEADER VIEW =====
        val tvAuthor = findViewById<TextView>(R.id.tvAuthor)
        val tvContentPost = findViewById<TextView>(R.id.tvContent)
        val tvCommunityTag = findViewById<TextView>(R.id.tv_community_tag)
        val tvTime = findViewById<TextView>(R.id.tvTime)
        val tvLikeCount = findViewById<TextView>(R.id.tv_like_count)
        val tvCommentCount = findViewById<TextView>(R.id.tv_comment_count)
        val ivLikeIcon = findViewById<ImageView>(R.id.iv_like_icon)

        tvAuthor.text = author
        tvContentPost.text = content
        tvCommunityTag.text = communityTag
        tvTime.text = time
        tvLikeCount.text = likeCount.toString()
        tvCommentCount.text = commentCount.toString()

        ivLikeIcon.setImageResource(
            if (isLiked)
                R.drawable.ic_favorite_filled
            else
                R.drawable.ic_favorite
        )

        // ===== LIKE CLICK (SYNC WITH HOME) =====
        ivLikeIcon.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val res = DataRepository.toggleLike(postId)

                    isLiked = res.isLiked
                    likeCount = res.like_count

                    ivLikeIcon.setImageResource(
                        if (isLiked)
                            R.drawable.ic_favorite_filled
                        else
                            R.drawable.ic_favorite
                    )

                    tvLikeCount.text = likeCount.toString()

                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        this@ReplyActivity,
                        "Gagal update like",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        // ===== TAG COLOR =====
        val colorInt = getTagColor(this, communityTag)
        val bg = tvCommunityTag.background
        if (bg != null) {
            val wrapped = DrawableCompat.wrap(bg).mutate()
            DrawableCompat.setTint(wrapped, colorInt)
            tvCommunityTag.background = wrapped
        } else {
            tvCommunityTag.setBackgroundColor(colorInt)
        }

        tvCommunityTag.setTextColor(
            if (isColorDark(colorInt))
                ContextCompat.getColor(this, R.color.white)
            else
                ContextCompat.getColor(this, R.color.text_dark)
        )

        // ===== REPLIES LIST =====
        rvReplies = findViewById(R.id.rvReplies)
        rvReplies.layoutManager = LinearLayoutManager(this)
        adapter = ReplyAdapter(mutableListOf())
        rvReplies.adapter = adapter

        loadReplies()

        // ===== INPUT REPLY =====
        val etReply = findViewById<EditText>(R.id.etReply)
        val btnSend = findViewById<ImageButton>(R.id.btnSend)
        val btnPickReplyImage = findViewById<ImageButton>(R.id.btnPickReplyImage)
        val btnRemoveReplyImage = findViewById<ImageButton>(R.id.btnRemoveReplyImage)
        val imgReplyPreview = findViewById<ImageView>(R.id.imgReplyPreview)

        val pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
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
                Toast.makeText(
                    this,
                    "Tulis teks atau pilih gambar dulu",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            sendReply(text, etReply, imgReplyPreview, btnRemoveReplyImage)
        }
    }

    // ===== LOAD REPLIES =====
    private fun loadReplies() {
        lifecycleScope.launch {
            try {
                val replies = DataRepository.getReplies(postId)
                adapter.updateData(replies.toMutableList())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // ===== SEND REPLY =====
    private fun sendReply(
        text: String,
        etReply: EditText,
        imgReplyPreview: ImageView,
        btnRemoveReplyImage: ImageButton
    ) {
        lifecycleScope.launch {
            try {
                val imageFile = selectedImageUri?.let { uriToFile(it) }

                val success = DataRepository.addReply(
                    postId = postId,
                    author = username,
                    content = text,
                    imageFile = imageFile
                )

                if (success) {
                    etReply.setText("")
                    selectedImageUri = null
                    imgReplyPreview.visibility = View.GONE
                    btnRemoveReplyImage.visibility = View.GONE

                    loadReplies()
                    rvReplies.scrollToPosition(adapter.itemCount - 1)
                } else {
                    Toast.makeText(
                        this@ReplyActivity,
                        "Gagal mengirim balasan",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@ReplyActivity,
                    "Terjadi kesalahan",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun uriToFile(uri: Uri): File {
        val inputStream = contentResolver.openInputStream(uri)
        val file = File(cacheDir, "reply_${System.currentTimeMillis()}.jpg")

        FileOutputStream(file).use { output ->
            inputStream?.copyTo(output)
        }

        return file
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finish()
            true
        } else super.onOptionsItemSelected(item)
    }
}
