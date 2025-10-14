package com.example.pawpals.community

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pawpals.data.DataRepository
import com.example.pawpals.R

class ReplyActivity : AppCompatActivity() {

    private lateinit var rvReplies: RecyclerView
    private lateinit var adapter: ReplyAdapter
    private lateinit var postId: String

    // image picking
    private var selectedImageUri: Uri? = null
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reply)

        // set toolbar sebagai actionbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Balasan"

        postId = intent.getStringExtra("post_id") ?: ""
        val postTitle = intent.getStringExtra("post_title") ?: ""
        val postContent = intent.getStringExtra("post_content") ?: ""

        val tvTitle = findViewById<TextView>(R.id.tvPostTitle)
        val tvContent = findViewById<TextView>(R.id.tvPostContent)
        rvReplies = findViewById(R.id.rvReplies)
        val etReply = findViewById<EditText>(R.id.etReply)
        val btnSend = findViewById<ImageButton>(R.id.btnSend)
        val btnPickReplyImage = findViewById<ImageButton>(R.id.btnPickReplyImage)
        val btnRemoveReplyImage = findViewById<ImageButton>(R.id.btnRemoveReplyImage)
        val imgReplyPreview = findViewById<ImageView>(R.id.imgReplyPreview)

        tvTitle.text = postTitle
        tvContent.text = postContent

        rvReplies.layoutManager = LinearLayoutManager(this)
        val repliesList = DataRepository.getReplies(postId)
        adapter = ReplyAdapter(repliesList)
        rvReplies.adapter = adapter

        // setup launcher pilih gambar (GetContent -> returns Uri)
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

        // tombol pilih gambar
        btnPickReplyImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // tombol hapus preview
        btnRemoveReplyImage.setOnClickListener {
            selectedImageUri = null
            imgReplyPreview.setImageDrawable(null)
            imgReplyPreview.visibility = View.GONE
            btnRemoveReplyImage.visibility = View.GONE
        }

        // aksi kirim balasan (kirim juga imageUri sebagai String)
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
                imageUri = selectedImageUri?.toString() // <-- PENTING: konversi Uri? -> String?
            )

            adapter.notifyItemInserted(DataRepository.getReplies(postId).lastIndex)
            rvReplies.scrollToPosition(DataRepository.getReplies(postId).lastIndex)

            // reset input
            etReply.setText("")
            selectedImageUri = null
            imgReplyPreview.setImageDrawable(null)
            imgReplyPreview.visibility = View.GONE
            btnRemoveReplyImage.visibility = View.GONE
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
