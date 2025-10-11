package com.example.pawpals

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment // PENTING: Pastikan ini ada
import androidx.appcompat.app.AppCompatActivity
// HAPUS: import androidx.appcompat.widget.Toolbar (Toolbar sekarang diakses via binding)
// HAPUS: import com.google.android.material.floatingactionbutton.FloatingActionButton (Tidak perlu lagi)
import com.example.pawpals.databinding.ActivityMainBinding
import com.example.pawpals.ui.EventsListFragment
import com.example.pawpals.ui.ProfileFragment

// PASTIKAN SEMUA FRAGMENT INI SUDAH ADA IMPORTNYA:
/* import com.example.pawpals.ui.HomeFragment
 import com.example.pawpals.ui.EventsListFragment
 import com.example.pawpals.ui.ProfileFragment
  import com.example.pawpals.ModelFragment
 import com.example.pawpals.CommunityListFragment
 import com.example.pawpals.AdminFragment*/

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    // HAPUS fabAddPost: sekarang akses langsung via binding.fabDeteksi
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
        setSupportActionBar(binding.toolbar)
//        supportActionBar?.title = "PawPals"
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        // SharedPreferences cek role
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val role = prefs.getString("USER_ROLE", "user")
        Log.d("MainActivity", "Role terbaca: $role")

        val initialFragment = if (role == "admin") {
            AdminFragment()
        } else {
            HomeFragment()
        }

        // 1. Load Fragment Awal
        if (savedInstanceState == null) {
            loadFragment(initialFragment) // Gunakan fungsi loadFragment() di sini
        }

        // 2. Bottom Navigation Logic
        // 2. Bottom Navigation Logic
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    if (role == "admin") {
                        loadFragment(AdminFragment()) // admin diarahkan ke AdminFragment
                    } else {
                        loadFragment(HomeFragment()) // user biasa ke HomeFragment
                    }
                }
                R.id.nav_community -> loadFragment(CommunityListFragment())
                R.id.nav_event -> loadFragment(com.example.pawpals.ui.EventsListFragment())
                R.id.nav_model -> loadFragment(com.example.pawpals.ModelFragment())
                R.id.nav_profile -> loadFragment(com.example.pawpals.ui.ProfileFragment())
                else -> false
            }
            true
        }

        // 3. Floating Action Button (NEW POST)
        // Gunakan ID FAB yang benar dari layout XML kamu (misal: fabDeteksi atau fabAddPost)
        binding.fabAdd.setOnClickListener {

            // Tampilkan pesan konfirmasi (opsional)
            Toast.makeText(this, "Membuka halaman Buat Post Baru", Toast.LENGTH_SHORT).show()

            // ⭐️ LOGIKA KRUSIAL: Buka NewPostActivity ⭐️
            val intent = Intent(this, NewPostActivity::class.java)

            // Kirim category saat ini agar Post tahu ia harus masuk ke forum mana
            intent.putExtra("category", currentCategory)

            // Jalankan Activity baru dan tunggu hasilnya (RESULT_OK) untuk refresh list
            newPostLauncher.launch(intent)
        }
    }

    // Fungsi loadFragment yang baru dan lebih rapi
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

        // Matikan tombol back di semua fragment
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }



    // Fungsi loadPosts disederhanakan, ID lama fragment_container DIBUANG
    private fun loadPosts(category: String) {
        val fragment = supportFragmentManager.findFragmentById(R.id.main_fragment_container) // GANTI ID di sini
        if (fragment is CommunityListFragment) {
            fragment.reloadData(category) // pastikan kamu buat fungsi reloadData di CommunityListFragment
        }
    }

    // ... (onSupportNavigateUp dan logout dibiarkan) ...

    /* HAPUS SEMUA FUNGSI NAVIGASI LAMA INI KARENA SUDAH DIGANTIKAN OLEH loadFragment DI ONCREATE:
    fun openEventsFragment() { ... }
    fun openCommunityFragment() { ... }
    fun openProfileFragment() { ... }
    */
}