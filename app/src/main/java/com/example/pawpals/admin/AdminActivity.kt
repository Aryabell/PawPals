package com.example.pawpals.admin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.navigation.NavigationView
import com.example.pawpals.R
import com.example.pawpals.admin.fragments.*

class AdminActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_sidebar)

        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navView)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open,
            R.string.close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Default fragment
        replaceFragment(DashboardFragment())

        // Sidebar navigation click handler
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_dashboard -> replaceFragment(DashboardFragment())
                R.id.nav_members -> replaceFragment(MemberFragment())
                R.id.nav_events -> replaceFragment(EventFragment())
                R.id.nav_announcements -> replaceFragment(AnnouncementFragment())
                R.id.nav_posts -> replaceFragment(PostFragment())
                R.id.nav_logout -> {
                    // Tampilan konfirmasi sebelum logout
                    AlertDialog.Builder(this)
                        .setTitle("Konfirmasi Logout")
                        .setMessage("Apakah kamu yakin ingin keluar dari akun admin?")
                        .setPositiveButton("Ya") { _, _ ->
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finish() // Tutup AdminActivity
                        }
                        .setNegativeButton("Batal", null)
                        .show()
                }
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
