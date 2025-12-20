package com.example.pawpals.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pawpals.R
import com.example.pawpals.api.NotificationApiClient
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventNotificationsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotificationAdapter
    private lateinit var swipeRefresh: SwipeRefreshLayout

    private val eventNotificationList = mutableListOf<NotificationModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_event_notifications, container, false)

        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        recyclerView = view.findViewById(R.id.recyclerViewEvent)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = NotificationAdapter(eventNotificationList)
        recyclerView.adapter = adapter

        // 🔄 Swipe refresh
        swipeRefresh.setOnRefreshListener {
            loadEventNotifications()
        }

        loadEventNotifications()

        return view
    }

    override fun onResume() {
        super.onResume()
        loadEventNotifications()
    }

    private fun loadEventNotifications() {
        swipeRefresh.isRefreshing = true

        val prefs = requireContext()
            .getSharedPreferences("user_session", android.content.Context.MODE_PRIVATE)

        val userId = prefs.getString("user_id", null)?.toInt() ?: run {
            swipeRefresh.isRefreshing = false
            return
        }

        NotificationApiClient.api
            .getNotificationsByType(userId, "event")
            .enqueue(object : Callback<List<NotificationModel>> {

                override fun onResponse(
                    call: Call<List<NotificationModel>>,
                    response: Response<List<NotificationModel>>
                ) {
                    eventNotificationList.clear()
                    eventNotificationList.addAll(response.body() ?: emptyList())
                    adapter.notifyDataSetChanged()
                    swipeRefresh.isRefreshing = false
                }

                override fun onFailure(call: Call<List<NotificationModel>>, t: Throwable) {
                    swipeRefresh.isRefreshing = false
                    t.printStackTrace()
                }
            })
    }
}
