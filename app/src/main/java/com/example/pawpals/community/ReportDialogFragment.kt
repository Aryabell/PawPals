package com.example.pawpals.community

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment

class ReportDialogFragment : DialogFragment() {

    companion object {
        private const val ARG_POST_ID = "post_id"

        fun newInstance(postId: String): ReportDialogFragment {
            return ReportDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_POST_ID, postId)
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val postId = arguments?.getString(ARG_POST_ID).orEmpty()

        val reasons = arrayOf(
            "Melanggar peraturan komunitas",
            "Saya tidak tertarik dengan postingan ini",
            "Mencurigakan atau spam",
            "Menghina atau berbahaya",
            "Ekspresi mencelakai diri / bunuh diri"
        )

        return AlertDialog.Builder(requireContext())
            .setTitle("Laporkan Postingan")
            .setItems(reasons) { _, which ->
                val reason = reasons[which]

                // TODO: Implement report API
                Toast.makeText(
                    requireContext(),
                    "Laporan dikirim: $reason",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("Batal", null)
            .create()
    }
}
