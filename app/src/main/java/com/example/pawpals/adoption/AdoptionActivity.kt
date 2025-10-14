package com.example.pawpals.adoption

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pawpals.R
import com.example.pawpals.databinding.ActivityAdoptionBinding

class AdoptionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdoptionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdoptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // === SETUP GRID ===
        binding.rvDogs.layoutManager = GridLayoutManager(this, 2)
        val dogs = SampleDogs.list
        val adapter = DogAdapter(dogs) { dog ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("dog", dog)
            startActivity(intent)
        }
        binding.rvDogs.adapter = adapter

        // === HAMBURGER ===
        binding.btnMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        // === NAVIGATION DRAWER ===
        binding.navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_profile -> Toast.makeText(this, "Buka Profil", Toast.LENGTH_SHORT).show()
                R.id.nav_notifications -> Toast.makeText(this, "Buka Notifikasi", Toast.LENGTH_SHORT).show()
                R.id.nav_adoption -> Toast.makeText(this, "Kamu sudah di halaman Adopsi 🐶", Toast.LENGTH_SHORT).show()
                R.id.nav_settings -> Toast.makeText(this, "Buka Pengaturan", Toast.LENGTH_SHORT).show()
                R.id.nav_logout -> Toast.makeText(this, "Keluar dari akun", Toast.LENGTH_SHORT).show()
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // ✅ Handle gesture-back & tombol back secara modern
        onBackPressedDispatcher.addCallback(this) {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }
}
