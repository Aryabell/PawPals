package com.example.pawpals.admin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.pawpals.R
import com.example.pawpals.api.ApiClient
import com.example.pawpals.data.DataRepository
import com.example.pawpals.data.EventRepository
import com.example.pawpals.data.MemberRepository
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private lateinit var txtStats: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val v = inflater.inflate(R.layout.fragment_dashboard, container, false)
        txtStats = v.findViewById(R.id.txtStats)

        lifecycleScope.launch {
            val stats = ApiClient.instance.getDashboardStats()

            txtStats.text = """
            👥 Total Member: ${stats.total_members}
            🧑 Pengurus: ${stats.pengurus}
            👤 Member: ${stats.member}
            📅 Event: ${stats.events}
            ⭐ Postingan Trending: ${stats.trending_posts}
        """.trimIndent()
        }

        return v
    }


    private fun observeLiveData() {
        // Observasi perubahan data
        DataRepository.posts.observe(viewLifecycleOwner, Observer { updateStats() })
        EventRepository.events.observe(viewLifecycleOwner, Observer { updateStats() })
    }

    private fun updateStats() {
        val members = MemberRepository.getMembers()

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
