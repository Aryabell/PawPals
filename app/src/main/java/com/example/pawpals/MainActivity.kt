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
import com.example.pawpals.admin.fragments.ModelFragment
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

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val ivNotification = findViewById<ImageView>(R.id.iv_notification)
        ivNotification.setOnClickListener {
            loadFragment(NotificationFragment())
        }


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
                    AlertDialog.Builder(this)
                        .setTitle("Konfirmasi Logout")
                        .setMessage("Apakah kamu yakin ingin keluar dari akun?")
                        .setPositiveButton("Ya") { _, _ ->
                            Toast.makeText(this, "Keluar dari akun", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .setNegativeButton("Batal", null)
                        .show()
                }
            }


            binding.drawerLayout.closeDrawers()
            true


        }


        setupNavHeader()


        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

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
            Toast.makeText(this, "Membuka halaman Buat Post Baru", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, NewPostActivity::class.java)
            intent.putExtra("category", currentCategory)
            newPostLauncher.launch(intent)
        }


        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                if (supportFragmentManager.backStackEntryCount > 0) {

                    val currentFragment = supportFragmentManager.findFragmentById(R.id.main_fragment_container)

                    if (currentFragment is EventDetailFragment) {
                        supportFragmentManager.popBackStack()

                        supportActionBar?.show()
                        binding.toolbar.visibility = View.VISIBLE
                        binding.bottomNavigation.visibility = View.VISIBLE
                        findViewById<ImageView>(R.id.iv_notification)?.visibility = View.VISIBLE // Tampilkan Notifikasi


                        supportActionBar?.title = "Paw Event"

                        drawerToggle.isDrawerIndicatorEnabled = true

                    } else {
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                        isEnabled = true
                    }
                } else {

                    if (binding.drawerLayout.isDrawerOpen(binding.navigationView)) {
                        binding.drawerLayout.closeDrawer(binding.navigationView)
                    } else {
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
        })
    }

    private fun setupNavHeader() {
        val headerView = binding.navigationView.getHeaderView(0)


        val tvDisplayName = headerView.findViewById<TextView>(R.id.tv_display_name)
        val tvUsername = headerView.findViewById<TextView>(R.id.tv_username)

        tvDisplayName.text = "Paw Admin" // Contoh Display Name
        tvUsername.text = "@minpaw"     // Contoh Username

    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_container, fragment)
            .commit()

        drawerToggle.toolbarNavigationClickListener = View.OnClickListener {
            binding.drawerLayout.openDrawer(binding.navigationView)
        }
        drawerToggle.isDrawerIndicatorEnabled = true
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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

                binding.bottomNavigation.visibility = View.GONE

                drawerToggle.isDrawerIndicatorEnabled = false
                supportActionBar?.setDisplayHomeAsUpEnabled(true)


                binding.toolbar.setNavigationOnClickListener {
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
                binding.bottomNavigation.visibility = View.VISIBLE
                binding.fabAdd.hide()
                drawerToggle.isDrawerIndicatorEnabled = true
                drawerToggle.setToolbarNavigationClickListener(null)
            }
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

    private fun loadPosts(category: String) {
        val fragment = supportFragmentManager.findFragmentById(R.id.main_fragment_container)
        if (fragment is CommunityListFragment) {
            fragment.reloadData(category)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.main_fragment_container)

        if (currentFragment is ProfileFragment) {
            loadFragment(HomeFragment())

            supportActionBar?.title = "PawPals"
            binding.bottomNavigation.visibility = View.VISIBLE
            binding.bottomNavigation.selectedItemId = R.id.nav_home // Memastikan Home terpilih

            drawerToggle.isDrawerIndicatorEnabled = true

            binding.toolbar.setNavigationOnClickListener {
                binding.drawerLayout.openDrawer(binding.navigationView)
            }
            return true

        } else if (binding.drawerLayout.isDrawerOpen(binding.navigationView)) {
            binding.drawerLayout.closeDrawer(binding.navigationView)
            return true
        } else {
            binding.drawerLayout.openDrawer(binding.navigationView)
            return true
        }
    }
}