package com.example.pawpals.event

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pawpals.R
import com.example.pawpals.databinding.FragmentEventDetailBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class EventDetailFragment : Fragment(R.layout.fragment_event_detail) {

    companion object {
        private const val ARG_ID = "event_id"
        fun newInstance(id: Int) = EventDetailFragment().apply {
            arguments = Bundle().apply { putInt(ARG_ID, id) }
        }
    }

    private var _b: FragmentEventDetailBinding? = null
    private val b get() = _b!!
    private lateinit var viewModel: EventViewModel
    private var eventId: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _b = FragmentEventDetailBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity()).get(EventViewModel::class.java)
        eventId = arguments?.getInt(ARG_ID) ?: 0

        b.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()   // kembali ke fragment sebelumnya
        }

        viewModel.events.observe(viewLifecycleOwner) { list ->
            val ev = list.find { it.id == eventId } ?: return@observe
            b.tvTitle.text = ev.title
            b.tvDate.text = ev.date
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

            if (ev.isJoined) {
                b.btnJoin.text = getString(R.string.joined)
                b.btnJoin.isEnabled = false
                b.btnCancelJoin.visibility = View.VISIBLE
            } else {
                b.btnJoin.text = getString(R.string.join)
                b.btnJoin.isEnabled = true
                b.btnCancelJoin.visibility = View.GONE
            }

            b.btnJoin.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.confirm))
                    .setMessage(getString(R.string.confirm_join, ev.title))
                    .setPositiveButton(R.string.join) { _, _ ->
                        viewModel.joinEvent(ev.id)
                        Toast.makeText(requireContext(), "Joined: ${ev.title}", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton(R.string.cancel, null)
                    .show()
            }

            b.btnCancelJoin.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.confirm))
                    .setMessage(getString(R.string.confirm_cancel, ev.title))
                    .setPositiveButton(R.string.cancel_join) { _, _ ->
                        viewModel.cancelJoin(ev.id)
                        Toast.makeText(requireContext(), "Cancelled: ${ev.title}", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton(R.string.back, null)
                    .show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
