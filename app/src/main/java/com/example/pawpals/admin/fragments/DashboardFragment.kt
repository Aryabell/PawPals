package com.example.pawpals.admin.fragments

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.pawpals.R
import com.example.pawpals.data.DataRepository
import com.example.pawpals.data.EventRepository
import com.example.pawpals.data.MemberRepository
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private lateinit var txtStats: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.fragment_dashboard, container, false)
        txtStats = v.findViewById(R.id.txtStats)

        loadStats()

        return v
    }

    private fun loadStats() {
        lifecycleScope.launch {
            val members = MemberRepository.getAllMembers()

            val totalUsers = members.size
            val pengurus = members.count { it.role.equals("Pengurus", true) }
            val memberCount = members.count { it.role.equals("Member", true) }

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
}
