package com.example.pawpals.community

import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.example.pawpals.R
import com.example.pawpals.data.DataRepository
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class NewPostActivity : AppCompatActivity() {

    private lateinit var spinnerCategory: Spinner
    private lateinit var imgPreview: ImageView
    private lateinit var btnRemoveImage: Button

    private var selectedImageUri: Uri? = null
    private lateinit var username: String
    private lateinit var role: String


    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
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

        val prefs = getSharedPreferences("user_session", MODE_PRIVATE)
        username = prefs.getString("username", "Anon") ?: "Anon"
        role = prefs.getString("role", "user") ?: "user"

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Buat Post Baru"

        val etContent = findViewById<EditText>(R.id.etContent)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val btnPickImage = findViewById<Button>(R.id.btnPickImage)

        spinnerCategory = findViewById(R.id.spinnerCategory)
        imgPreview = findViewById(R.id.imgPreview)
        btnRemoveImage = findViewById(R.id.btnRemoveImage)

        val categories = listOf(
            "Lost Dogs",
            "Paw Playground",
            "Adopsi",
            "Kesehatan",
            "Playdate",
            "Rekomendasi",
            "Events",
            "Talks"
        )

        spinnerCategory.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            categories
        )

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
                Toast.makeText(
                    this,
                    "Tulis teks atau pilih gambar dulu",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            submitPost(content, selectedCategory)
        }
    }

    private fun submitPost(content: String, category: String) {
        lifecycleScope.launch {
            try {
                val imageFile = selectedImageUri?.let { uri ->
                    uriToFile(uri)
                }

                val success = DataRepository.addPost(
                    content = content,
                    author = username,
                    category = category,
                    userRole = "user",
                    imageFile = imageFile
                )

                if (success) {
                    setResult(RESULT_OK)
                    finish()
                } else {
                    Toast.makeText(
                        this@NewPostActivity,
                        "Gagal mengirim post",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@NewPostActivity,
                    "Terjadi kesalahan",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun uriToFile(uri: Uri): File {
        val contentResolver = applicationContext.contentResolver
        val mime = contentResolver.getType(uri) ?: "image/jpeg"
        val ext = when (mime) {
            "image/png" -> ".png"
            "image/webp" -> ".webp"
            else -> ".jpg"
        }
        val file = File(cacheDir, "upload_${System.currentTimeMillis()}$ext")


        contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        } ?: throw IllegalStateException("Gagal membaca file dari URI")

        return file
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finish()
            true
        } else super.onOptionsItemSelected(item)
    }
}
