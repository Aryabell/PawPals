package com.example.pawpals.admin.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pawpals.R
import com.example.pawpals.event.EventAdapter
import com.example.pawpals.event.EventDetailFragment
import com.example.pawpals.event.EventViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EventFragment : Fragment() {

    private lateinit var viewModel: EventViewModel
    private lateinit var adapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.fragment_event, container, false)
        val recyclerView = v.findViewById<RecyclerView>(R.id.rvEvents)
        val fabAdd = v.findViewById<FloatingActionButton>(R.id.fabAddEvent)

        fabAdd.setOnClickListener {
            showAddEventDialog()
        }

        viewModel = ViewModelProvider(requireActivity())[EventViewModel::class.java]

        adapter = EventAdapter(
            items = emptyList(),
            onItemClick = { event ->
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, EventDetailFragment.newInstance(event.id))
                    .addToBackStack(null)
                    .commit()
            },
            onDeleteClick = { event ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Hapus Event")
                    .setMessage("Yakin ingin menghapus '${event.title}'?")
                    .setPositiveButton("Hapus") { _, _ ->
                        viewModel.deleteEvent(event.id)
                    }
                    .setNegativeButton("Batal", null)
                    .show()
            },
            isAdmin = true
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewModel.events.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }

        return v
    }

    private fun showAddEventDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_event, null)
        val etTitle = dialogView.findViewById<EditText>(R.id.etTitle)
        val etDesc = dialogView.findViewById<EditText>(R.id.etDesc)
        val etDate = dialogView.findViewById<EditText>(R.id.etDate)
        val etLocation = dialogView.findViewById<EditText>(R.id.etLocation)

        // 🗓️ Jadikan etDate buka date & time picker saat diklik
        etDate.apply {
            isFocusable = false
            isClickable = true

            setOnClickListener {
                val calendar = Calendar.getInstance()

                // Pilih tanggal
                val datePicker = DatePickerDialog(
                    requireContext(),
                    { _, year, month, dayOfMonth ->
                        calendar.set(Calendar.YEAR, year)
                        calendar.set(Calendar.MONTH, month)
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                        // Setelah pilih tanggal → pilih waktu
                        val timePicker = TimePickerDialog(
                            requireContext(),
                            { _, hourOfDay, minute ->
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                calendar.set(Calendar.MINUTE, minute)

                                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                                setText(formatter.format(calendar.time))
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                        )
                        timePicker.show()
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
                datePicker.show()
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Tambah Event Baru")
            .setView(dialogView)
            .setPositiveButton("Tambah") { _, _ ->
                val title = etTitle.text.toString().trim()
                val desc = etDesc.text.toString().trim()
                val date = etDate.text.toString().trim()
                val loc = etLocation.text.toString().trim()

                if (title.isEmpty() || desc.isEmpty() || date.isEmpty() || loc.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Semua field harus diisi",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    viewModel.addEvent(title, desc, date, loc)
                    Toast.makeText(
                        requireContext(),
                        "Event berhasil ditambahkan!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}
