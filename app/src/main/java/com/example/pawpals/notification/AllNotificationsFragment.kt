package com.example.pawpals.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pawpals.R

class AllNotificationsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotificationAdapter
    private val notificationList = mutableListOf<NotificationModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_all_notifications, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewAll)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = NotificationAdapter(notificationList)
        recyclerView.adapter = adapter

        // Dummy data contoh
        notificationList.add(NotificationModel("❤️ Fay menyukai postinganmu"))
        notificationList.add(NotificationModel("💬 Sae membalas postinganmu"))
        notificationList.add(NotificationModel("📅 2 hari lagi Event Playdate dimulai!"))
        adapter.notifyDataSetChanged()

        return view
    }
}
