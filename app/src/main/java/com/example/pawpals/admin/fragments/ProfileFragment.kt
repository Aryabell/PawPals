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

                selectedImageUri?.let { uri ->

                    // 1. Ambil izin akses persisten
                    val contentResolver = requireContext().contentResolver
                    val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION

                    // Coba ambil izin persisten.
                    try {
                        contentResolver.takePersistableUriPermission(uri, takeFlags)
                    } catch (e: SecurityException) {
                        // Jika gagal (misalnya bukan persistable URI), log error
                        e.printStackTrace()
                        Toast.makeText(requireContext(), "Gagal mendapatkan izin URI persisten.", Toast.LENGTH_LONG).show()
                        return@registerForActivityResult // Hentikan proses
                    }

                    // 2. Tampilkan gambar dan simpan ke prefs (hanya setelah izin berhasil)
                    b.imgProfile.setImageURI(uri)
                    val prefs = requireContext().getSharedPreferences("user_prefs", Activity.MODE_PRIVATE)
                    prefs.edit().putString("profile_pic", uri.toString()).apply()
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _b = FragmentProfileBinding.bind(view)

        val prefs = requireContext().getSharedPreferences("user_prefs", Activity.MODE_PRIVATE)
        val profilePicPath = prefs.getString("profile_pic", null)

        val savedName = prefs.getString("name", "Paw Admin")
        val savedUsername = prefs.getString("username", "@minpaw")
        val savedPassword = prefs.getString("password", "")

        // Tampilkan foto profil terakhir
        if (!profilePicPath.isNullOrEmpty()) {
            val imageUri = Uri.parse(profilePicPath)

            // Tampilkan gambar menggunakan Glide
            Glide.with(this)
                .load(imageUri)
                .error(R.drawable.ic_profile_placeholder) // FALLBACK JIKA GAGAL LOAD
                .circleCrop()
                .into(b.imgProfile)
        } else {
            // Tampilkan placeholder jika tidak ada URI tersimpan
            Glide.with(this)
                .load(R.drawable.ic_profile_placeholder)
                .circleCrop()
                .into(b.imgProfile)
        }

        // Tampilkan data tersimpan
        b.edtName.setText(savedName)
        // Tampilkan Username
        b.edtUsername.setText(savedUsername)
        b.edtPassword.setText(savedPassword)

        // Ganti foto
        b.btnChangePic.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        // Simpan perubahan profil
        b.btnSave.setOnClickListener {
            val name = b.edtName.text.toString().trim()
            val username = b.edtUsername.text.toString().trim() // Ambil Username
            val password = b.edtPassword.text.toString().trim()

            prefs.edit()
                .putString("name", name)
                .putString("username", username) // Simpan Username
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