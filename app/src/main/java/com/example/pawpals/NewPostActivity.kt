package com.example.pawpals

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class NewPostActivity : AppCompatActivity() {

    // menyimpan uri gambar yang dipilih user
    private var selectedImageUri: Uri? = null

    // view preview & button hapus
    private lateinit var imgPreview: ImageView
    private lateinit var btnRemoveImage: Button

    // ActivityResult API untuk pilih gambar
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            imgPreview.setImageURI(uri)
            imgPreview.visibility = View.VISIBLE
            btnRemoveImage.visibility = View.VISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)

        // toolbar custom
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Buat Post Baru"

        // ambil view yang diperlukan
        val etContent = findViewById<EditText>(R.id.etContent)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val btnPickImage = findViewById<Button>(R.id.btnPickImage)
        btnRemoveImage = findViewById(R.id.btnRemoveImage)
        imgPreview = findViewById(R.id.imgPreview)

        // masih ambil category untuk disimpan di repo,
        // tapi tidak men-set TextView tvCategory
        val category = intent.getStringExtra("category") ?: "Talks"

        // pilih gambar
        btnPickImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // hapus gambar
        btnRemoveImage.setOnClickListener {
            selectedImageUri = null
            imgPreview.setImageDrawable(null)
            imgPreview.visibility = View.GONE
            btnRemoveImage.visibility = View.GONE
        }

        // submit post
        btnSubmit.setOnClickListener {
            val content = etContent.text.toString().trim()
            if (content.isEmpty() && selectedImageUri == null) {
                Toast.makeText(this, "Tulis teks atau pilih gambar dulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            DataRepository.addPost(
                content = content,
                author = "You",
                category = category
            )

            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finish()
            true
        } else super.onOptionsItemSelected(item)
    }
}
