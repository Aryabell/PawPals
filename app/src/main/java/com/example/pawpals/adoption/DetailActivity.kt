package com.example.pawpals.adoption

import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import coil.load
import com.example.pawpals.R
import com.example.pawpals.databinding.ActivityDetailBinding
import com.example.pawpals.model.Dog
import com.example.pawpals.model.Gender

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        loadDogData()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbarDetail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
        binding.toolbarDetail.setNavigationOnClickListener { finish() }
    }

    private fun loadDogData() {
        val dog = intent.getParcelableExtra<Dog>("dog")
        if (dog == null) {
            finish()
            return
        }

        binding.apply {

            // === IMAGE ===
            imgDogLarge.load(dog.imageUrl) { crossfade(true) }

            // === MAIN INFO ===
            tvNameDetail.text = dog.name
            tvBreedDetail.text = dog.breed
            tvLocation.text = dog.location

            // === BADGES ===
            tvAge.text = "${dog.ageInYears} Tahun"
            tvWeight.text = "${dog.weightKg} kg"

            val genderText = if (dog.gender == Gender.MALE) "Jantan" else "Betina"
            tvGender.text = genderText
            tvGender.backgroundTintList = ContextCompat.getColorStateList(
                this@DetailActivity,
                if (dog.gender == Gender.MALE) R.color.primary_blue else R.color.pink_badge
            )
            tvGender.setTextColor(ContextCompat.getColor(this@DetailActivity, R.color.white))

            // === OWNER ===
            tvOwnerName.text = dog.ownerName
            tvOwnerHandle.text = "@${dog.ownerMessageHandle}"
            tvOwnerPhone.text = dog.ownerPhone

            // === BUTTON CALL ===
            btnCall.setOnClickListener {
                startActivity(
                    Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${dog.ownerPhone}")
                    }
                )
            }

            // === BUTTON MESSAGE ===
            btnMessage.setOnClickListener {
                startActivity(
                    Intent(this@DetailActivity, com.example.pawpals.message.ChatActivity::class.java).apply {
                        putExtra("receiverId", dog.ownerId)
                        putExtra("receiverName", dog.ownerName)
                        putExtra("dogName", dog.name)
                    }
                )
            }

            // === BUTTON ADOPT ===
            btnAdopt.setOnClickListener {
                startActivity(
                    Intent(this@DetailActivity, AdoptionFormActivity::class.java).apply {
                        putExtra("dogName", dog.name)
                        putExtra("dogId", dog.id)
                    }
                )
            }
        }
    }

    fun Int.dpToPx(): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            Resources.getSystem().displayMetrics
        ).toInt()
    }
}
