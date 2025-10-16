package com.example.pawpals.admin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.pawpals.R
import com.example.pawpals.data.DataRepository
import com.example.pawpals.data.EventRepository
import com.example.pawpals.data.MemberRepository

class DashboardFragment : Fragment() {

    private lateinit var txtStats: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.fragment_dashboard, container, false)
        txtStats = v.findViewById(R.id.txtStats)
        updateStats()
        observeLiveData()
        return v
    }

    private fun observeLiveData() {
        // Observasi perubahan di LiveData biar auto update
        DataRepository.posts.observe(viewLifecycleOwner, Observer { updateStats() })
        EventRepository.events.observe(viewLifecycleOwner, Observer { updateStats() })
    }

    private fun updateStats() {
        val members = MemberRepository.members
        val totalUsers = members.size
        val pengurus = members.count { it.role.equals("Pengurus", ignoreCase = true) }
        val memberCount = members.count { it.role.equals("Member", ignoreCase = true) }

        val totalEvents = EventRepository.events.value?.size ?: 0
        val trendingPosts = DataRepository.getTrendingPosts().size

        txtStats.text = """
            👥 Total Pengguna: $totalUsers
            🧑 Pengurus: $pengurus
            👤 Member: $memberCount
            📅 Event: $totalEvents
            ⭐ Postingan Trending: $trendingPosts
        """.trimIndent()
    }
}
