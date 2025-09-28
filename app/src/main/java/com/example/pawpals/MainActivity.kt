package com.example.pawpals

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pawpals.ui.EventsListFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, EventsListFragment())
                .commit()
        }
    }
}
