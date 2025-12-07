package com.example.pawpals

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
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
import android.widget.TextView
import com.example.pawpals.event.EventDetailFragment
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private var currentCategory = "Talks"
    private lateinit var drawerToggle: ActionBarDrawerToggle

    // launcher buat buka NewPostActivity
    private val newPostLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
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
                /*R.id.nav_home -> {
                    loadFragment(HomeFragment())
                }*/
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                }
                /*R.id.nav_notifications -> {
                    loadFragment(NotificationFragment())
                }*/
                R.id.nav_adoption -> {
                    loadFragment(AdoptionFragment())
                }

                R.id.nav_logout -> {
                    // Tampilkan konfirmasi sebelum logout
                    AlertDialog.Builder(this)
                        .setTitle("Konfirmasi Logout")
                        .setMessage("Apakah kamu yakin ingin keluar dari akun?")
                        .setPositiveButton("Ya") { _, _ ->
                            Toast.makeText(this, "Keluar dari akun", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finish() // Tutup MainActivity biar gak bisa balik pakai tombol back
                        }
                        .setNegativeButton("Batal", null)
                        .show()
                }
            }

            // Tutup drawer setelah klik menu
            binding.drawerLayout.closeDrawers()
            true


        }

        // Integrasi Header Navigation Drawer (Mengisi Nama Pengguna)
        setupNavHeader()

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

        // Tambahkan block ini untuk menangani tombol Back secara terpusat
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Cek apakah kita bisa pop back stack (yaitu saat di EventDetailFragment)
                if (supportFragmentManager.backStackEntryCount > 0) {

                    // Cek fragment yang akan di-pop (Jika EventDetail)
                    val currentFragment = supportFragmentManager.findFragmentById(R.id.main_fragment_container)

                    if (currentFragment is EventDetailFragment) {
                        // 1. Pop Fragment dari BackStack
                        supportFragmentManager.popBackStack()

                        // 2. Kembalikan UI state Toolbar/ActionBar
                        supportActionBar?.show()
                        binding.toolbar.visibility = View.VISIBLE // Tampilkan kembali toolbar
                        binding.bottomNavigation.visibility = View.VISIBLE
                        findViewById<ImageView>(R.id.iv_notification)?.visibility = View.VISIBLE // Tampilkan Notifikasi

                        // 3. Atur ulang Title Toolbar (Asumsi kembali ke EventsListFragment)
                        supportActionBar?.title = "Paw Event"

                        // 4. Pastikan Hamburger Icon kembali
                        drawerToggle.isDrawerIndicatorEnabled = true

                    } else {
                        // Jika bukan EventDetail, gunakan penanganan back default
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                        isEnabled = true
                    }
                } else {
                    // Jika tidak ada back stack lagi (di Home/List Utama), keluar/tutup drawer
                    if (binding.drawerLayout.isDrawerOpen(binding.navigationView)) {
                        binding.drawerLayout.closeDrawer(binding.navigationView)
                    } else {
                        isEnabled = false // Wajib diset false sebelum memanggil super
                        onBackPressedDispatcher.onBackPressed() // Keluar aplikasi
                    }
                }
            }
        })
    }

    private fun setupNavHeader() {
        // 1. Ambil referensi ke header view (index 0 karena biasanya hanya ada satu header)
        val headerView = binding.navigationView.getHeaderView(0)

        // 2. Temukan TextViews berdasarkan ID yang sudah kamu buat di nav_header.xml
        // Ingat: ID-nya adalah R.id.tv_display_name dan R.id.tv_username
        val tvDisplayName = headerView.findViewById<TextView>(R.id.tv_display_name)
        val tvUsername = headerView.findViewById<TextView>(R.id.tv_username)

        // 3. Isi TextViews dengan data (Ini bisa diganti dengan data real dari SharedPrefs/Database)
        tvDisplayName.text = "Paw Admin" // Contoh Display Name
        tvUsername.text = "@minpaw"     // Contoh Username

        // Opsional: Ganti foto profil jika diperlukan
        // val imgProfile = headerView.findViewById<ImageView>(R.id.img_profile)
        // imgProfile.setImageResource(R.drawable.ava_admin_baru)
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_container, fragment)
            .commit()

        // Reset listener Toolbar ke Drawer Toggle
        drawerToggle.toolbarNavigationClickListener = View.OnClickListener {
            binding.drawerLayout.openDrawer(binding.navigationView)
        }
        drawerToggle.isDrawerIndicatorEnabled = true
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Default: Hamburger

        when (fragment) {
            is HomeFragment -> {
                supportActionBar?.title = "PawPals"
                binding.fabAdd.hide()
            }
            is CommunityListFragment -> {
                supportActionBar?.title = "Pals Komunitas"
                binding.fabAdd.show()
            }
            is EventsListFragment -> {
                supportActionBar?.title = "Paw Event"
                binding.fabAdd.hide()
            }
            is ModelFragment -> {
                supportActionBar?.title = "Deteksi Penyakit"
                binding.fabAdd.hide()
            }
            is ProfileFragment -> {
                supportActionBar?.title = "Paw Profile"
                binding.fabAdd.hide()

                // Sembunyikan Bottom Nav
                binding.bottomNavigation.visibility = View.GONE

                // Non-aktifkan Hamburger Icon & Atur Back Arrow
                drawerToggle.isDrawerIndicatorEnabled = false
                supportActionBar?.setDisplayHomeAsUpEnabled(true)

                // SET CUSTOM LISTENER untuk Back Arrow di ProfileFragment
                binding.toolbar.setNavigationOnClickListener {
                    // Panggil logika onSupportNavigateUp yang menangani ProfileFragment
                    onSupportNavigateUp()
                }
            }

            is MessageListFragment -> {
                supportActionBar?.title = "Pals Pesan"
                binding.fabAdd.hide()
            }
            is AdoptionFragment -> {
                supportActionBar?.title = "Paw Adopsi"
                binding.fabAdd.hide()
            }
            is NotificationFragment -> {
                supportActionBar?.title = "Notifikasi"
                binding.fabAdd.hide()
            }

            else -> {
                // Kasus default, pastikan Hamburger kembali
                binding.bottomNavigation.visibility = View.VISIBLE
                binding.fabAdd.hide()
                drawerToggle.isDrawerIndicatorEnabled = true
                // Kembalikan listener Toolbar ke Hamburger (drawerToggle.syncState() sudah menangani ini)
                drawerToggle.setToolbarNavigationClickListener(null) // Reset custom listener jika ada
            }
        }


        // supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // Fungsi untuk membuka detail event dan menyembunyikan Toolbar
    fun openEventDetail(eventId: Int) {
        val frag = EventDetailFragment.newInstance(eventId)

        // 1. Transaksi Fragment: Memuat EventDetailFragment ke container utama
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_container, frag) // Ganti R.id.main_fragment_container jika ID Anda berbeda
            .addToBackStack("EventDetail") // Tambahkan ke BackStack dengan tag
            .commit()

        // 2. Sembunyikan Komponen UI Utama (Hamburger, Title, Notifikasi, BottomNav)

        // Sembunyikan Toolbar/ActionBar (Hanya tombol back fragment yang akan terlihat)
        supportActionBar?.hide()
        binding.toolbar.visibility = View.GONE // Asumsi: toolbar diakses via binding

        // Sembunyikan Bottom Navigation
        binding.bottomNavigation.visibility = View.GONE

        // Sembunyikan Notifikasi (Jika ImageView notifikasi adalah view terpisah di layout Activity)
        // Asumsi ID ImageView notifikasi adalah iv_notification
        findViewById<ImageView>(R.id.iv_notification)?.visibility = View.GONE
    }

    private fun loadPosts(category: String) {
        val fragment = supportFragmentManager.findFragmentById(R.id.main_fragment_container)
        if (fragment is CommunityListFragment) {
            fragment.reloadData(category)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.main_fragment_container)

        if (currentFragment is ProfileFragment) {
            // Jika di ProfileFragment, kembali ke HomeFragment
            loadFragment(HomeFragment())

            // Atur ulang Toolbar dan Bottom Nav ke state Home
            supportActionBar?.title = "PawPals"
            binding.bottomNavigation.visibility = View.VISIBLE
            binding.bottomNavigation.selectedItemId = R.id.nav_home // Memastikan Home terpilih

            // Pulihkan Hamburger Icon
            drawerToggle.isDrawerIndicatorEnabled = true
            // Pastikan Toolbar menggunakan listener bawaan Toggle lagi
            binding.toolbar.setNavigationOnClickListener {
                binding.drawerLayout.openDrawer(binding.navigationView)
            }
            return true

        } else if (binding.drawerLayout.isDrawerOpen(binding.navigationView)) {
            // Jika Drawer terbuka, tutup Drawer
            binding.drawerLayout.closeDrawer(binding.navigationView)
            return true
        } else {
            // Default: buka Drawer (Hamburger)
            binding.drawerLayout.openDrawer(binding.navigationView)
            return true
        }
    }
}