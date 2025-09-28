package com.example.pawpals

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var fabAddPost: FloatingActionButton
    private var currentCategory = "Talks" // default category

    // launcher buat buka NewPostActivity
    private val newPostLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ){ result ->
        if (result.resultCode == Activity.RESULT_OK){
            // refresh fragment/list setelah ada post baru
            loadPosts(currentCategory)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // hubungkan Toolbar di layout dengan ActionBar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.title = "PawPals Forum Community"
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CommunityListFragment())
                .commit()
        }

        // hubungkan FAB
        fabAddPost = findViewById(R.id.fabAddPost)
        fabAddPost.setOnClickListener {
            val intent = Intent(this, NewPostActivity::class.java)
            intent.putExtra("category", currentCategory)
            newPostLauncher.launch(intent)
        }
    }

    private fun loadPosts(category: String) {
        // TODO: panggil function refresh list post di fragment
        // misalnya CommunityListFragment punya method reloadData()
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (fragment is CommunityListFragment) {
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
