package com.example.pawpals.event

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pawpals.MainActivity
import com.example.pawpals.R
import com.example.pawpals.databinding.FragmentEventsListBinding

class EventsListFragment : Fragment(R.layout.fragment_events_list) {

    private var _b: FragmentEventsListBinding? = null
    private val b get() = _b!!

    private lateinit var viewModel: EventViewModel
    private lateinit var adapter: EventAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _b = FragmentEventsListBinding.bind(view)

        viewModel = ViewModelProvider(requireActivity())[EventViewModel::class.java]

        adapter = EventAdapter(
            items = emptyList(),
            onJoinClick = { ev ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Konfirmasi")
                    .setMessage("Yakin mau join event \"${ev.title}\"?")
                    .setPositiveButton("Join") { _, _ ->
                        viewModel.joinEvent(ev.id)
                        Toast.makeText(requireContext(), "Joined: ${ev.title}", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Batal", null)
                    .show()
            },
            onItemClick = { ev ->
                (requireActivity() as MainActivity).openEventDetail(ev.id)
            },
            isAdmin = false
        )

        b.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        b.recyclerView.adapter = adapter

        // 🔥 OBSERVE SEKALI SAJA
        viewModel.events.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            b.swipeRefresh.isRefreshing = false
        }

        // 🔥 FETCH DATA DARI DATABASE
        viewModel.fetchEvents()

        // Swipe refresh
        b.swipeRefresh.setOnRefreshListener {
            viewModel.refreshEvents()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
