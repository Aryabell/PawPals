package com.example.pawpals

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.ActionBarDrawerToggle
import com.example.pawpals.adoption.AdoptionFragment
import com.example.pawpals.community.CommunityListFragment
import com.example.pawpals.community.NewPostActivity
import com.example.pawpals.databinding.ActivityMainBinding
import com.example.pawpals.event.EventsListFragment
import com.example.pawpals.message.MessageListFragment
import com.example.pawpals.model.ModelFragment
import com.example.pawpals.ui.HomeFragment
import com.example.pawpals.ui.ProfileFragment
import com.example.pawpals.notification.NotificationFragment
import com.example.pawpals.admin.LoginActivity
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

        val ivNotification = findViewById<ImageView>(R.id.iv_notification)
        ivNotification.setOnClickListener {
            loadFragment(NotificationFragment())
        }


        // 🔹 Setup Drawer
        drawerToggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        binding.navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                }
                R.id.nav_notifications -> {
                    loadFragment(NotificationFragment())
                }
                R.id.nav_adoption -> {
                    loadFragment(AdoptionFragment())
                }
                R.id.nav_settings -> {
                    loadFragment(SettingsFragment())
                }
                R.id.nav_logout -> {
                    Toast.makeText(this, "Keluar dari akun", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish() // biar MainActivity ditutup
                }
            }

            // Tutup drawer setelah klik menu
            binding.drawerLayout.closeDrawers()
            true
        }

        // 🔹 Load Fragment Awal (langsung ke Home)
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        // 🔹 Bottom Navigation
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> loadFragment(HomeFragment())
                R.id.nav_community -> loadFragment(CommunityListFragment())
                R.id.nav_event -> loadFragment(EventsListFragment())
                R.id.nav_model -> loadFragment(ModelFragment())
                R.id.nav_message -> loadFragment(MessageListFragment())
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
                supportActionBar?.title = "PawPals: Beranda"
                binding.fabAdd.hide()
            }
            is CommunityListFragment -> {
                supportActionBar?.title = "PawPals: Komunitas"
                binding.fabAdd.show()
            }
            is EventsListFragment -> {
                supportActionBar?.title = "PawPals: Event"
                binding.fabAdd.hide()
            }
            is ModelFragment -> {
                supportActionBar?.title = "PawPals: Deteksi Penyakit"
                binding.fabAdd.hide()
            }
            is ProfileFragment -> {
                supportActionBar?.title = "PawPals: Profile"
                binding.fabAdd.hide()
            }
            is MessageListFragment -> {
                supportActionBar?.title = "PawPals: Pesan"
                binding.fabAdd.hide()
            }
            is AdoptionFragment -> {
                supportActionBar?.title = "PawPals: Adopsi Anjing"
                binding.fabAdd.hide()
            }
            is NotificationFragment -> {
                supportActionBar?.title = "PawPals: Notifikasi"
                binding.fabAdd.hide()
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
