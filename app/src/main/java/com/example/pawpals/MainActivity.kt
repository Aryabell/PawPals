package com.example.pawpals

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.pawpals.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val role = prefs.getString("USER_ROLE", "user")
        Log.d("MainActivity", "Role terbaca: $role")

        val fragment = if (role == "admin") {
            AdminFragment()
        } else {
            HomeFragment()
        }

        // Tampilkan fragment utama (admin atau home)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.fragmentContainer.id, fragment)
                .commit()
        }
    }

    // Fungsi logout yang bisa dipanggil dari fragment
    fun logout() {
        getSharedPreferences("user_prefs", MODE_PRIVATE).edit().clear().apply()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    // Fungsi untuk buka EventsListFragment dari mana saja
    fun openEventsFragment() {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, com.example.pawpals.ui.EventsListFragment())
            .addToBackStack(null) // supaya bisa back ke fragment sebelumnya
            .commit()
    }
}
