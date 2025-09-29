package com.example.pawpals.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pawpals.R
import com.example.pawpals.databinding.FragmentEventsListBinding

class EventsListFragment : Fragment(R.layout.fragment_events_list) {

    private var _b: FragmentEventsListBinding? = null
    private val b get() = _b!!
    private lateinit var viewModel: EventViewModel
    private lateinit var adapter: EventAdapter

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = "Events for Pals"
            setDisplayHomeAsUpEnabled(false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _b = FragmentEventsListBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity()).get(EventViewModel::class.java)

        adapter = EventAdapter(emptyList(),
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
                val frag = EventDetailFragment.newInstance(ev.id)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_fragment_container, frag)
                    .addToBackStack(null)
                    .commit()
            }
        )

        b.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        b.recyclerView.adapter = adapter

        viewModel.events.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }

        b.fabAdd.setOnClickListener {
            Toast.makeText(requireContext(), "Feature add event nanti ya", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
