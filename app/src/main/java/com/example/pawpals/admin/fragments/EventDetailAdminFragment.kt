package com.example.pawpals.admin.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.pawpals.R
import com.example.pawpals.databinding.FragmentEventDetailAdminBinding
import com.example.pawpals.event.EventViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class EventDetailAdminFragment : Fragment(R.layout.fragment_event_detail_admin) {

    companion object {
        private const val ARG_ID = "event_id"

        fun newInstance(id: Int) = EventDetailAdminFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_ID, id)
            }
        }
    }

    private var _b: FragmentEventDetailAdminBinding? = null
    private val b get() = _b!!
    private lateinit var viewModel: EventViewModel
    private var eventId: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _b = FragmentEventDetailAdminBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity())[EventViewModel::class.java]
        eventId = arguments?.getInt(ARG_ID) ?: 0

        viewModel.fetchEvents()

        // ==============================
        // BACK
        // ==============================
        b.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // ==============================
        // OBSERVE SATU EVENT (BY ID)
        // ==============================
        viewModel.getEventById(eventId).observe(viewLifecycleOwner) { ev ->
            ev ?: return@observe

            b.tvTitle.text = ev.title
            b.tvDate.text = formatDate(ev.date)
            b.tvLocation.text = ev.location
            b.tvDesc.text = ev.description

            Glide.with(this)
                .load(ev.imageUrl)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_error)
                .centerCrop()
                .thumbnail(0.25f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(b.ivBannerDetail)

            // ==============================
            // DELETE BUTTON
            // ==============================
            b.btnDelete.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("Hapus Event")
                    .setMessage("Yakin ingin menghapus event \"${ev.title}\"?")
                    .setPositiveButton("Hapus") { _, _ ->
                        viewModel.deleteEvent(ev.id)
                        Toast.makeText(
                            requireContext(),
                            "Event berhasil dihapus",
                            Toast.LENGTH_SHORT
                        ).show()
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                    .setNegativeButton("Batal", null)
                    .show()
            }
        }
    }


    private fun formatDate(dateStr: String): String {
        return try {
            val input = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val output = SimpleDateFormat("dd MMM yyyy • HH:mm", Locale("id", "ID"))
            output.format(input.parse(dateStr)!!)
        } catch (e: Exception) {
            dateStr
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
