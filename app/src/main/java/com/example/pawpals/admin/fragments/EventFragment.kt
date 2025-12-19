package com.example.pawpals.admin.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pawpals.MainActivity
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

    // ==============================
    // IMAGE PICKER
    // ==============================
    private var selectedImageUri: Uri? = null

    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            selectedImageUri = uri
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val v = inflater.inflate(R.layout.fragment_event, container, false)
        val recyclerView = v.findViewById<RecyclerView>(R.id.rvEvents)
        val fabAdd = v.findViewById<FloatingActionButton>(R.id.fabAddEvent)

        viewModel = ViewModelProvider(requireActivity())[EventViewModel::class.java]
        viewModel.fetchEvents()

        adapter = EventAdapter(
            items = emptyList(),
            onItemClick = { ev ->
                (requireActivity() as MainActivity).openEventDetailAdmin(ev.id)
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

        fabAdd.setOnClickListener {
            showAddEventDialog()
        }

        return v
    }

    // ==============================
    // ADD EVENT DIALOG
    // ==============================
    private fun showAddEventDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_event, null)

        val etTitle = dialogView.findViewById<EditText>(R.id.etTitle)
        val etDesc = dialogView.findViewById<EditText>(R.id.etDesc)
        val etDate = dialogView.findViewById<EditText>(R.id.etDate)
        val etLocation = dialogView.findViewById<EditText>(R.id.etLocation)
        val btnPickImage = dialogView.findViewById<Button>(R.id.btnPickImage)

        selectedImageUri = null

        // ==============================
        // PICK IMAGE
        // ==============================
        btnPickImage.setOnClickListener {
            imagePicker.launch("image/*")
        }

        // ==============================
        // DATE & TIME PICKER
        // ==============================
        etDate.isFocusable = false
        etDate.setOnClickListener {
            val calendar = Calendar.getInstance()

            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    calendar.set(year, month, day)

                    TimePickerDialog(
                        requireContext(),
                        { _, hour, minute ->
                            calendar.set(Calendar.HOUR_OF_DAY, hour)
                            calendar.set(Calendar.MINUTE, minute)

                            val formatter =
                                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            etDate.setText(formatter.format(calendar.time))
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                    ).show()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // ==============================
        // DIALOG
        // ==============================
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
                    return@setPositiveButton
                }

                viewModel.addEvent(
                    requireContext(),
                    title,
                    desc,
                    date,
                    loc,
                    selectedImageUri
                )

                Toast.makeText(
                    requireContext(),
                    "Event berhasil ditambahkan!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}
