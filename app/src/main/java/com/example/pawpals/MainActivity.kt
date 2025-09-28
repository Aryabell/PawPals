package com.example.pawpals

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.pawpals.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fabAddPost: FloatingActionButton
    private var currentCategory = "Talks" // default category

    // launcher buat buka NewPostActivity
    private val newPostLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // refresh fragment/list setelah ada post baru
            loadPosts(currentCategory)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "PawPals Forum Community"
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        // SharedPreferences cek role
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val role = prefs.getString("USER_ROLE", "user")
        Log.d("MainActivity", "Role terbaca: $role")

        val fragment = if (role == "admin") {
            AdminFragment()
        } else {
            HomeFragment()
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.fragmentContainer.id, fragment)
                .commit()
        }

        // Bottom navigation
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(binding.fragmentContainer.id, HomeFragment())
                        .commit()
                    true
                }
                R.id.nav_event -> {
                    openEventsFragment()
                    true
                }
                R.id.nav_community -> {
                    openCommunityFragment()
                    true
                }

                R.id.nav_profile -> {
                    openProfileFragment()
                    true
                }
                else -> false
            }
        }

        // Floating Action Button
        fabAddPost = findViewById(R.id.fabAddPost)
        fabAddPost.setOnClickListener {
            val intent = Intent(this, NewPostActivity::class.java)
            intent.putExtra("category", currentCategory)
            newPostLauncher.launch(intent)
        }
    }

    private fun loadPosts(category: String) {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (fragment is CommunityListFragment) {
            fragment.reloadData(category) // pastikan kamu buat fungsi reloadData di CommunityListFragment
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    // 🔹 Fungsi logout
    fun logout() {
        getSharedPreferences("user_prefs", MODE_PRIVATE).edit().clear().apply()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    // 🔹 Buka EventsListFragment
    fun openEventsFragment() {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, com.example.pawpals.ui.EventsListFragment())
            .addToBackStack(null)
            .commit()
    }

    // 🔹 Buka CommunityListFragment
    fun openCommunityFragment() {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, CommunityListFragment())
            .addToBackStack(null)
            .commit()
    }

    fun openProfileFragment() {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, com.example.pawpals.ui.ProfileFragment())
            .addToBackStack(null)
            .commit()
    }
}
