package com.example.pawpals.adoption

import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.ViewGroup.LayoutParams
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
        supportActionBar?.title = "" // Menghilangkan judul

        binding.toolbarDetail.setNavigationOnClickListener {
            // Menggunakan finish() untuk kembali ke Activity sebelumnya
            finish()
        }

        // Ambil data Dog dari intent
        val dog = intent.getParcelableExtra<Dog>("dog") ?: return

        with(binding) {
            imgDogLarge.load(dog.imageUrl) { crossfade(true) }
            tvNameDetail.text = dog.name

            // 1. Tampilkan Jenis Anjing
            tvBreedDetail.text = dog.breed

            // 2. Tambahkan TextView Lokasi secara dinamis
            // Lokasi target: header_container (LinearLayout vertical)
            // Asumsi binding memiliki referensi headerContainer (jika Anda menggunakan ViewBinding dengan ConstraintLayout)
            val parentLayout = root.findViewById<LinearLayout>(R.id.header_container)
            parentLayout?.let {
                // Cek apakah LocationView sudah ada
                if (it.findViewById<TextView>(R.id.tvLocationDynamic) == null) {

                    val locationView = TextView(this@DetailActivity).apply {
                        id = R.id.tvLocationDynamic // Beri ID agar bisa dicari
                        text = dog.location
                        textSize = 14f

                        // Menggunakan ContextCompat untuk warna
                        setTextColor(ContextCompat.getColor(context, R.color.gray_light))

                        // Icon lokasi (pastikan ic_location ada di drawable)
                        setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location, 0, 0, 0)
                        compoundDrawablePadding = 4.dpToPx()
                        layoutParams = LinearLayout.LayoutParams(
                            LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT
                        ).apply {
                            topMargin = 2.dpToPx()
                        }
                    }

                    // Tambahkan locationView setelah tvBreedDetail
                    it.addView(locationView, it.indexOfChild(tvBreedDetail) + 1)
                }
            }


            // ISI DATA KE BADGE
            tvAge.text = "${dog.ageInYears} Tahun"

            // Logika GENDER di sini
            val genderText = if (dog.gender == Gender.MALE) "Jantan" else "Betina"
            tvGender.text = genderText

            // Penyesuaian warna Badge (Jika ingin diatur via kode)
            val genderBadgeColor = if (dog.gender == Gender.MALE) R.color.primary_blue else R.color.pink_badge
            tvGender.setBackgroundResource(R.drawable.rounded_light) // Asumsi rounded_light adalah background badge
            tvGender.backgroundTintList = ContextCompat.getColorStateList(this@DetailActivity, genderBadgeColor)
            tvGender.setTextColor(ContextCompat.getColor(this@DetailActivity, R.color.white)) // Teks putih untuk badge warna


            tvWeight.text = "${dog.weightKg} kg"

            // OWNER DETAILS (OwnerName dan OwnerHandle)
            tvOwnerName.text = dog.ownerName
            tvOwnerHandle.text = "@${dog.ownerMessageHandle}"

            // Listener Tombol
            btnCall.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${dog.ownerPhone}")
                }
                startActivity(intent)
            }

            btnMessage.setOnClickListener {
                val intent = Intent(this@DetailActivity, com.example.pawpals.message.ChatActivity::class.java)
                intent.putExtra("receiverId", dog.ownerId)
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

    // --------------------------------------------------------
    // EXTENSION FUNCTION
    // --------------------------------------------------------
    // extension function untuk konversi dp ke px
    fun Int.dpToPx(): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            Resources.getSystem().displayMetrics
        ).toInt()
    }
}