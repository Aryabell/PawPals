package com.example.pawpals.admin.fragments

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.pawpals.R

class AnnouncementFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_announcement, container, false)

        val txt = v.findViewById<EditText>(R.id.txtAnnouncement)
        val btn = v.findViewById<Button>(R.id.btnSendAnnouncement)

        btn.setOnClickListener {
            val msg = txt.text.toString()
            if (msg.isNotEmpty()) {
                Toast.makeText(requireContext(), "📢 Pengumuman dikirim: $msg", Toast.LENGTH_SHORT).show()
                txt.text.clear()
            }
        }

        return v
    }
}
