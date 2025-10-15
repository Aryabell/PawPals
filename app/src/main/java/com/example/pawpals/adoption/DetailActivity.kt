package com.example.pawpals.adoption

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.example.pawpals.R
import com.example.pawpals.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup toolbar dengan tombol back
        setSupportActionBar(binding.toolbarDetail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back) // pastikan file ini ada di res/drawable
        supportActionBar?.title = "Pals Adoption"

        binding.toolbarDetail.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Ambil data Dog dari intent
        val dog = intent.getParcelableExtra<Dog>("dog") ?: return

        with(binding) {
            imgDogLarge.load(dog.imageUrl) { crossfade(true) }
            tvNameDetail.text = dog.name
            tvBreedDetail.text = "${dog.breed} • ${dog.location}"
            tvAge.text = "${dog.ageInYears} years"
            tvWeight.text = "${dog.weightKg} kg"
            tvOwnerName.text = dog.ownerName
            tvOwnerHandle.text = dog.ownerMessageHandle

            btnCall.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${dog.ownerPhone}")
                }
                startActivity(intent)
            }

            btnMessage.setOnClickListener {
                val intent = Intent(this@DetailActivity, com.example.pawpals.message.ChatActivity::class.java)
                intent.putExtra("receiverId", dog.ownerId) // ID user pemilik anjing
                intent.putExtra("receiverName", dog.ownerName)
                intent.putExtra("dogName", dog.name)
                startActivity(intent)
            }


            btnAdopt.setOnClickListener {
                val intent = Intent(this@DetailActivity, AdoptionFormActivity::class.java)
                intent.putExtra("dogName", dog.name)
                startActivity(intent)
            }

        }
    }
}
