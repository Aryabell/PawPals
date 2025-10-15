package com.example.pawpals.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.pawpals.R
import com.example.pawpals.databinding.FragmentProfileBinding

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var _b: FragmentProfileBinding? = null
    private val b get() = _b!!

    private var selectedImageUri: Uri? = null

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                selectedImageUri = result.data?.data
                b.imgProfile.setImageURI(selectedImageUri)

                // Simpan sementara ke prefs
                val prefs = requireContext().getSharedPreferences("user_prefs", Activity.MODE_PRIVATE)
                prefs.edit().putString("profile_pic", selectedImageUri.toString()).apply()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _b = FragmentProfileBinding.bind(view)

        val prefs = requireContext().getSharedPreferences("user_prefs", Activity.MODE_PRIVATE)
        val profilePicPath = prefs.getString("profile_pic", null)
        val savedName = prefs.getString("name", "")
        val savedPassword = prefs.getString("password", "")

        // Tampilkan foto profil terakhir
        if (!profilePicPath.isNullOrEmpty()) {
            b.imgProfile.setImageURI(Uri.parse(profilePicPath))
        } else {
            Glide.with(this)
                .load(R.drawable.ic_profile_placeholder)
                .circleCrop()
                .into(b.imgProfile)
        }

        // Tampilkan nama & password tersimpan
        b.edtName.setText(savedName)
        b.edtPassword.setText(savedPassword)

        // Ganti foto
        b.btnChangePic.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        // Simpan perubahan profil ke SharedPreferences
        b.btnSave.setOnClickListener {
            val name = b.edtName.text.toString().trim()
            val password = b.edtPassword.text.toString().trim()

            prefs.edit()
                .putString("name", name)
                .putString("password", password)
                .apply()

            Toast.makeText(requireContext(), "Profil disimpan", Toast.LENGTH_SHORT).show()
        }

        // Hilangkan tombol logout (nonaktif)
        b.btnLogout.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
