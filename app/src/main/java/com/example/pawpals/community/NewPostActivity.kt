package com.example.pawpals.community

import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.pawpals.data.DataRepository
import com.example.pawpals.R

class NewPostActivity : AppCompatActivity() {
    private lateinit var spinnerCategory: Spinner

    private var selectedImageUri: Uri? = null


    private lateinit var imgPreview: ImageView
    private lateinit var btnRemoveImage: Button


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


        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Buat Post Baru"

        val etContent = findViewById<EditText>(R.id.etContent)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val btnPickImage = findViewById<Button>(R.id.btnPickImage)
        btnRemoveImage = findViewById(R.id.btnRemoveImage)
        imgPreview = findViewById(R.id.imgPreview)
        spinnerCategory = findViewById(R.id.spinnerCategory)


        val categories = listOf(
            "Lost Dogs", "Paw Playground", "Adopsi",
            "Kesehatan", "Playdate", "Rekomendasi", "Events", "Talks"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            categories
        )
        spinnerCategory.adapter = adapter


        val initialCategory = intent.getStringExtra("category") ?: "Talks"
        val initialPosition = categories.indexOf(initialCategory)
        if (initialPosition >= 0) {
            spinnerCategory.setSelection(initialPosition)
        }


        btnPickImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }


        btnRemoveImage.setOnClickListener {
            selectedImageUri = null
            imgPreview.setImageDrawable(null)
            imgPreview.visibility = View.GONE
            btnRemoveImage.visibility = View.GONE
        }


        btnSubmit.setOnClickListener {
            val content = etContent.text.toString().trim()
            val selectedCategory = spinnerCategory.selectedItem.toString()

            if (content.isEmpty() && selectedImageUri == null) {
                Toast.makeText(this, "Tulis teks atau pilih gambar dulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            DataRepository.addPost(
                content = content,
                author = "Kamu",
                category = selectedCategory,
                imageUri = selectedImageUri?.toString()
            )

            setResult(RESULT_OK)
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