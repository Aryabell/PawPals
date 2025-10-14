package com.example.pawpals

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.example.pawpals.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.example.pawpals.ui.EventsListFragment
import com.example.pawpals.ui.ProfileFragment

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private var currentCategory = "Talks"
    private lateinit var drawerToggle: ActionBarDrawerToggle

    // launcher buat buka NewPostActivity
    private val newPostLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            loadPosts(currentCategory)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 🔹 Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // 🔹 Setup Drawer
        drawerToggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,   // pastikan ID-nya ada di XML
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        binding.navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                }
                R.id.nav_community -> {
                    loadFragment(CommunityListFragment())
                }
                R.id.nav_settings -> {
                    loadFragment(SettingsFragment()) // pastikan kamu punya fragment ini
                }
                R.id.nav_logout -> {
                    Toast.makeText(this, "Logout berhasil", Toast.LENGTH_SHORT).show()
                    // contoh logika logout sederhana
                    val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
                    prefs.edit().clear().apply()

                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

            // Tutup drawer setelah klik menu
            binding.drawerLayout.closeDrawers()
            true
        }


        // 🔹 SharedPreferences cek role
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val role = prefs.getString("USER_ROLE", "user")
        Log.d("MainActivity", "Role terbaca: $role")

        val initialFragment = if (role == "admin") {
            AdminFragment()
        } else {
            HomeFragment()
        }

        // 🔹 Load Fragment Awal
        if (savedInstanceState == null) {
            loadFragment(initialFragment)
        }

        // 🔹 Bottom Navigation
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    if (role == "admin") {
                        loadFragment(AdminFragment())
                    } else {
                        loadFragment(HomeFragment())
                    }
                }
                R.id.nav_community -> {
                    loadFragment(CommunityListFragment())
                    binding.bottomNavigation.selectedItemId = R.id.nav_community
                }

                R.id.nav_event -> loadFragment(EventsListFragment())
                R.id.nav_model -> loadFragment(ModelFragment())
            }
            true
        }

        // 🔹 Floating Action Button
        binding.fabAdd.setOnClickListener {
            Toast.makeText(this, "Membuka halaman Buat Post Baru", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, NewPostActivity::class.java)
            intent.putExtra("category", currentCategory)
            newPostLauncher.launch(intent)
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_container, fragment)
            .commit()

        when (fragment) {
            is HomeFragment -> {
                supportActionBar?.title = "PawPals"
            }
            is CommunityListFragment -> {
                supportActionBar?.title = "Pals Community"
            }
            is EventsListFragment -> {
                supportActionBar?.title = "Events for Pals"
            }
            is ModelFragment -> {
                supportActionBar?.title = "Disease Detection"
            }
            is ProfileFragment -> {
                supportActionBar?.title = "My Profile"
            }
            is AdminFragment -> {
                supportActionBar?.title = "Admin Panel"
            }
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun loadPosts(category: String) {
        val fragment = supportFragmentManager.findFragmentById(R.id.main_fragment_container)
        if (fragment is CommunityListFragment) {
            fragment.reloadData(category)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        if (binding.drawerLayout.isDrawerOpen(binding.navigationView)) {
            binding.drawerLayout.closeDrawer(binding.navigationView)
        } else {
            binding.drawerLayout.openDrawer(binding.navigationView)
        }
        return true
    }
}
