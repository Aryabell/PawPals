package com.example.pawpals

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.pawpals.adoption.AdoptionFragment
import com.example.pawpals.admin.LoginActivity
import com.example.pawpals.admin.fragments.EventDetailAdminFragment
import com.example.pawpals.admin.fragments.ModelFragment
import com.example.pawpals.community.CommunityListFragment
import com.example.pawpals.community.NewPostActivity
import com.example.pawpals.databinding.ActivityMainBinding
import com.example.pawpals.event.EventDetailFragment
import com.example.pawpals.event.EventsListFragment
import com.example.pawpals.message.MessageListFragment
import com.example.pawpals.notification.NotificationFragment
import com.example.pawpals.ui.HomeFragment
import com.example.pawpals.ui.ProfileFragment

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private var currentCategory = "Talks"
    lateinit var drawerToggle: ActionBarDrawerToggle

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

        // 1. Setup Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        // 2. Setup Notifikasi
        val ivNotification = findViewById<ImageView>(R.id.iv_notification)
        ivNotification.setOnClickListener {
            loadFragment(NotificationFragment())
        }

        // 3. Setup Drawer
        drawerToggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        // 4. Setup Sidebar Navigation
        binding.navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_profile -> loadFragment(ProfileFragment())
                R.id.nav_adoption -> loadFragment(AdoptionFragment())
                R.id.nav_logout -> showLogoutDialog()
            }
            binding.drawerLayout.closeDrawers()
            true
        }

        updateNavHeader()

        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        // 5. Setup Bottom Navigation
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

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, NewPostActivity::class.java)
            intent.putExtra("category", currentCategory)
            newPostLauncher.launch(intent)
        }

        // 6. Handle Back Button
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentFragment = supportFragmentManager.findFragmentById(R.id.main_fragment_container)

                if (currentFragment is EventDetailFragment || currentFragment is EventDetailAdminFragment) {
                    supportFragmentManager.popBackStack()
                    restoreDefaultUI()
                } else if (currentFragment is ProfileFragment || currentFragment is ModelFragment) {
                    loadFragment(HomeFragment()) // Balik ke Home
                    binding.bottomNavigation.selectedItemId = R.id.nav_home
                } else {
                    if (binding.drawerLayout.isDrawerOpen(binding.navigationView)) {
                        binding.drawerLayout.closeDrawer(binding.navigationView)
                    } else {
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                        isEnabled = true
                    }
                }
            }
        })
    }

    private fun getToolbarHeight(): Int {
        val styledAttributes = theme.obtainStyledAttributes(
            intArrayOf(android.R.attr.actionBarSize)
        )
        val toolbarHeight = styledAttributes.getDimension(0, 0f).toInt()
        styledAttributes.recycle()
        return toolbarHeight
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_container, fragment)
            .commit()

        // 1. Reset Style Default (Putih & Bersih)
        binding.bottomNavigation.visibility = View.VISIBLE
        binding.fabAdd.hide()
        binding.toolbar.visibility = View.VISIBLE
        supportActionBar?.show()
        findViewById<ImageView>(R.id.iv_notification)?.visibility = View.VISIBLE

        // Reset Warna Toolbar ke Putih & Teks Hitam
        binding.toolbar.setBackgroundColor(androidx.core.content.ContextCompat.getColor(this, R.color.white))
        binding.toolbar.setTitleTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.black))
        drawerToggle.drawerArrowDrawable.color = androidx.core.content.ContextCompat.getColor(this, R.color.black)

        if (fragment is ModelFragment) {
            binding.mainFragmentContainer.setPadding(0, 0, 0, 0)
        } else {
            binding.mainFragmentContainer.setPadding(0, getToolbarHeight(), 0, 0)
        }


        when (fragment) {
            is HomeFragment -> supportActionBar?.title = "PawPals"

            is CommunityListFragment -> {
                supportActionBar?.title = "Pals Komunitas"
                binding.fabAdd.show()
            }
            is EventsListFragment -> supportActionBar?.title = "Paw Event"
            is MessageListFragment -> supportActionBar?.title = "Pals Pesan"
            is AdoptionFragment -> supportActionBar?.title = "Paw Adopsi"
            is NotificationFragment -> supportActionBar?.title = "Notifikasi"

            is ProfileFragment -> {
                supportActionBar?.title = "Paw Profile"
                binding.bottomNavigation.visibility = View.GONE
            }

            // SETUP KHUSUS PAW CHECK (Kamera)
            is ModelFragment -> {
                supportActionBar?.title = "Paw Check"
                binding.bottomNavigation.visibility = View.GONE

                // Bikin Transparan
                binding.toolbar.setBackgroundColor(android.graphics.Color.TRANSPARENT)
                binding.toolbar.setTitleTextColor(android.graphics.Color.WHITE)
                drawerToggle.drawerArrowDrawable.color = android.graphics.Color.WHITE

                // Pastikan toolbar di layer paling atas
                binding.toolbar.bringToFront()
            }
        }

        setupToolbarIcon(fragment)
    }

    private fun setupToolbarIcon(fragment: Fragment) {
        // Daftar halaman yg pake tombol BACK
        val isBackMode = fragment is ProfileFragment ||
                fragment is ModelFragment ||
                fragment is AdoptionFragment ||
                fragment is NotificationFragment

        if (isBackMode) {
            // Mode Back Arrow
            drawerToggle.isDrawerIndicatorEnabled = false
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

            drawerToggle.setToolbarNavigationClickListener {
                // Kalau dipencet, balik ke Home
                loadFragment(HomeFragment())
                binding.bottomNavigation.selectedItemId = R.id.nav_home
            }

        } else {
            // Mode Burger Menu
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            drawerToggle.isDrawerIndicatorEnabled = true
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            drawerToggle.syncState()
            drawerToggle.setToolbarNavigationClickListener(null)
        }
    }


    fun updateNavHeader() {
        val headerView = binding.navigationView.getHeaderView(0)
        val tvDisplayName = headerView.findViewById<TextView>(R.id.tv_display_name)
        val tvUsername = headerView.findViewById<TextView>(R.id.tv_username)
        val imgProfile = headerView.findViewById<ImageView>(R.id.img_profile)

        val prefs = getSharedPreferences("user_session", MODE_PRIVATE)
        val username = prefs.getString("username", "Paw Admin")
        val handle = prefs.getString("handle", "@minpaw")
        val profilePicPath = prefs.getString("profile_pic", null)

        tvDisplayName.text = username
        tvUsername.text = handle

        if (!profilePicPath.isNullOrEmpty()) {
            com.bumptech.glide.Glide.with(this)
                .load(android.net.Uri.parse(profilePicPath))
                .circleCrop()
                .error(R.drawable.ava_paw)
                .into(imgProfile)
        } else {
            imgProfile.setImageResource(R.drawable.ava_paw)
        }
    }

    fun restoreDefaultUI() {
        supportActionBar?.show()
        binding.toolbar.visibility = View.VISIBLE
        binding.bottomNavigation.visibility = View.VISIBLE
        findViewById<ImageView>(R.id.iv_notification)?.visibility = View.VISIBLE
        supportActionBar?.title = "Paw Event"

        // Reset warna toolbar (Safety)
        binding.toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        binding.toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.black))
        drawerToggle.drawerArrowDrawable.color = ContextCompat.getColor(this, R.color.black)

        drawerToggle.isDrawerIndicatorEnabled = true
        drawerToggle.syncState()
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Logout")
            .setMessage("Apakah kamu yakin ingin keluar dari akun?")
            .setPositiveButton("Ya") { _, _ ->
                val prefs = getSharedPreferences("user_session", MODE_PRIVATE)
                prefs.edit().clear().apply()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun loadPosts(category: String) {
        val fragment = supportFragmentManager.findFragmentById(R.id.main_fragment_container)
        if (fragment is CommunityListFragment) {
            fragment.reloadData(category)
        }
    }

    fun openEventDetail(eventId: Int) {
        val frag = EventDetailFragment.newInstance(eventId)
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_container, frag)
            .addToBackStack("EventDetail")
            .commit()

        supportActionBar?.hide()
        binding.toolbar.visibility = View.GONE
        binding.bottomNavigation.visibility = View.GONE
        findViewById<ImageView>(R.id.iv_notification)?.visibility = View.GONE
    }

    fun openEventDetailAdmin(eventId: Int) {
        val frag = EventDetailAdminFragment.newInstance(eventId)
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_container, frag)
            .addToBackStack("EventDetailAdmin")
            .commit()
        supportActionBar?.hide()
        binding.toolbar.visibility = View.GONE
        binding.bottomNavigation.visibility = View.GONE
        findViewById<ImageView>(R.id.iv_notification)?.visibility = View.GONE
    }
}