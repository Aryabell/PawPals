package com.example.pawpals.notification

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class NotificationTabAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AllNotificationsFragment()
            1 -> EventNotificationsFragment()
            else -> AllNotificationsFragment()
        }
    }
}
