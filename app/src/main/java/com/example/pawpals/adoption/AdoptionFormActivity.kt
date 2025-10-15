package com.example.pawpals.adoption

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pawpals.R
import com.example.pawpals.databinding.ActivityAdoptionFormBinding

class AdoptionFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdoptionFormBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdoptionFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar
        setSupportActionBar(binding.toolbarForm)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Formulir Adopsi"
        binding.toolbarForm.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val dogName = intent.getStringExtra("dogName") ?: "Anjing"

        // Tombol kirim
        binding.btnSubmit.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val address = binding.etAddress.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val reason = binding.etReason.text.toString().trim()
            val agree = binding.cbAgree.isChecked

            if (name.isEmpty() || address.isEmpty() || phone.isEmpty() || reason.isEmpty()) {
                Toast.makeText(this, "Semua kolom wajib diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!agree) {
                Toast.makeText(this, "Anda harus menyetujui persyaratan adopsi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(
                this,
                "Formulir untuk mengadopsi $dogName telah dikirim! 🐶",
                Toast.LENGTH_LONG
            ).show()

            finish()
        }
    }
}
