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

        val etContent = findViewById<EditText>(R.id.etContent) // Sekarang Material TextInputEditText
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val btnPickImage = findViewById<Button>(R.id.btnPickImage)
        btnRemoveImage = findViewById(R.id.btnRemoveImage)
        imgPreview = findViewById(R.id.imgPreview)
        spinnerCategory = findViewById(R.id.spinnerCategory) // ⭐️ INI BARU ⭐️

        // ⭐️ LOGIKA SPINNER BARU ⭐️
        val categories = listOf(
            "Lost Dogs", "Paw Playground", "Adoption",
            "Health", "Playdate", "Recommend", "Events", "Talks"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            categories
        )
        spinnerCategory.adapter = adapter

        // Set nilai default spinner berdasarkan category yang dibawa dari MainActivity (jika ada)
        val initialCategory = intent.getStringExtra("category") ?: "Talks"
        val initialPosition = categories.indexOf(initialCategory)
        if (initialPosition >= 0) {
            spinnerCategory.setSelection(initialPosition)
        }

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

            // ⭐️ AMBIL KATEGORI YANG DIPILIH DARI SPINNER ⭐️
            val selectedCategory = spinnerCategory.selectedItem.toString()

            if (content.isEmpty() && selectedImageUri == null) {
                Toast.makeText(this, "Tulis teks atau pilih gambar dulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            DataRepository.addPost(
                content = content,
                author = "You", // Ganti dengan user yang login
                category = selectedCategory // ⭐️ GUNAKAN NILAI SPINNER ⭐️
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
