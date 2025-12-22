package com.example.pawpals.community

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.pawpals.R
import com.example.pawpals.data.DataRepository
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class NewPostActivity : AppCompatActivity() {

    // Sesuaikan tipe variabel dengan komponen Material di XML
    private lateinit var spinnerCategory: Spinner
    private lateinit var etContent: TextInputEditText // Ganti EditText biasa jadi TextInputEditText
    private lateinit var imgPreview: ShapeableImageView // Ganti ImageView biasa
    private lateinit var btnPickImage: MaterialButton
    private lateinit var btnRemoveImage: MaterialButton // Tombol hapus (opsional)
    private lateinit var btnSubmit: MaterialButton

    private var selectedImageUri: Uri? = null
    private lateinit var username: String
    private lateinit var role: String

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                imgPreview.setImageURI(uri)
                imgPreview.visibility = View.VISIBLE
                // btnRemoveImage.visibility = View.VISIBLE // Nyalain ini kalau di XML tombol hapusnya ada
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)

        // 1. SETUP USER DATA
        val prefs = getSharedPreferences("user_session", MODE_PRIVATE)
        username = prefs.getString("username", "Anon") ?: "Anon"
        role = prefs.getString("role", "user") ?: "user"

        // 2. SETUP TOOLBAR & BACK BUTTON
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Karena di XML udah pasang app:navigationIcon="@drawable/ic_arrow_back"
        // Kita tinggal tangani klik-nya aja di sini:
        toolbar.setNavigationOnClickListener {
            // Aksi kalau tombol back ditekan: Tutup Activity
            finish()
        }

        // 3. INIT VIEW (Sesuai ID di XML baru)
        etContent = findViewById(R.id.etContent)
        btnSubmit = findViewById(R.id.btnSubmit)
        btnPickImage = findViewById(R.id.btnPickImage)
        // btnRemoveImage = findViewById(R.id.btnRemoveImage) // Uncomment kalau ada di XML

        spinnerCategory = findViewById(R.id.spinnerCategory)
        imgPreview = findViewById(R.id.imgPreview)

        // 4. SETUP SPINNER
        val categories = listOf(
            "Lost Dogs", "Paw Playground", "Adopsi",
            "Kesehatan", "Playdate", "Rekomendasi",
            "Events", "Talks"
        )
        // Pake layout simple_spinner_item biar rapi
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter

        // Set Default Kategori dari Intent (kalau ada)
        val initialCategory = intent.getStringExtra("category") ?: "Talks"
        val initialPosition = categories.indexOf(initialCategory)
        if (initialPosition >= 0) {
            spinnerCategory.setSelection(initialPosition)
        }

        // 5. LISTENERS
        btnPickImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        /* Kalau mau pake tombol hapus gambar:
        btnRemoveImage.setOnClickListener {
            selectedImageUri = null
            imgPreview.setImageDrawable(null)
            imgPreview.visibility = View.GONE
            btnRemoveImage.visibility = View.GONE
        }
        */

        btnSubmit.setOnClickListener {
            val content = etContent.text.toString().trim()
            val selectedCategory = spinnerCategory.selectedItem.toString()

            if (content.isEmpty() && selectedImageUri == null) {
                Toast.makeText(this, "Tulis teks atau pilih gambar dulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Disable tombol biar gak kepencet 2x pas loading
            btnSubmit.isEnabled = false
            btnSubmit.text = "Mengirim..."

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
                    userRole = role, // Pake variable role yg udah diambil di atas
                    imageFile = imageFile
                )

                if (success) {
                    Toast.makeText(this@NewPostActivity, "Berhasil terkirim! ✅", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                } else {
                    Toast.makeText(this@NewPostActivity, "Gagal mengirim post ❌", Toast.LENGTH_SHORT).show()
                    // Balikin tombol submit
                    btnSubmit.isEnabled = true
                    btnSubmit.text = "Posting Sekarang"
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@NewPostActivity, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
                btnSubmit.isEnabled = true
                btnSubmit.text = "Posting Sekarang"
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
}