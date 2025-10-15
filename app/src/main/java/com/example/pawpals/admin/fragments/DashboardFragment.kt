package com.example.pawpals.admin.fragments

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.pawpals.R

class DashboardFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val txt = v.findViewById<TextView>(R.id.txtStats)
        txt.text = """
            👥 Total Pengguna: 4
            🧑 Pengurus: 1
            👤 Member: 3
            📅 Event: 2
            ⭐ Postingan Trending: 1
        """.trimIndent()
        return v
    }
}
