package com.example.pawpals.admin.fragments

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.pawpals.R
import com.example.pawpals.api.AnnouncementResponse
import com.example.pawpals.api.AnnouncementApiClient
import com.example.pawpals.api.AnnouncementApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AnnouncementFragment : Fragment() {

    private lateinit var txtAnnouncement: EditText
    private lateinit var btnSend: Button
    private lateinit var service: AnnouncementApiService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_announcement, container, false)

        txtAnnouncement = view.findViewById(R.id.txtAnnouncement)
        btnSend = view.findViewById(R.id.btnSendAnnouncement)

        service = AnnouncementApiClient
            .retrofit
            .create(AnnouncementApiService::class.java)

        btnSend.setOnClickListener {
            val message = txtAnnouncement.text.toString().trim()

            if (message.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Pengumuman tidak boleh kosong",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            sendAnnouncement(message)
        }

        return view
    }

    private fun sendAnnouncement(message: String) {
        btnSend.isEnabled = false

        service.sendAnnouncement(message)
            .enqueue(object : Callback<AnnouncementResponse> {

                override fun onResponse(
                    call: Call<AnnouncementResponse>,
                    response: Response<AnnouncementResponse>
                ) {
                    btnSend.isEnabled = true

                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(
                            requireContext(),
                            "📢 Pengumuman berhasil dikirim",
                            Toast.LENGTH_SHORT
                        ).show()
                        txtAnnouncement.text.clear()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "❌ Gagal mengirim pengumuman",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<AnnouncementResponse>, t: Throwable) {
                    btnSend.isEnabled = true
                    Toast.makeText(
                        requireContext(),
                        "❌ Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}
