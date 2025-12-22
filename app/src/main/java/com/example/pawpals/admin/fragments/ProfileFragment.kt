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

    // Gunakan nama file yang SAMA PERSIS dengan LoginActivity
    private val PREF_NAME = "user_session"

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                selectedImageUri = result.data?.data

                selectedImageUri?.let { uri ->
                    val contentResolver = requireContext().contentResolver
                    val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION

                    try {
                        contentResolver.takePersistableUriPermission(uri, takeFlags)
                    } catch (e: SecurityException) {
                        e.printStackTrace()
                    }

                    // Tampilkan gambar langsung
                    b.imgProfile.setImageURI(uri)

                    // Simpan URI gambar ke session
                    val prefs = requireContext().getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE)
                    prefs.edit().putString("profile_pic", uri.toString()).apply()
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _b = FragmentProfileBinding.bind(view)

        // 1. Panggil SharedPreferences "user_session" (bukan user_prefs)
        val prefs = requireContext().getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE)

        // 2. Ambil data sesuai key yang disimpan di LoginActivity
        // Di LoginActivity: prefs.putString("username", username) -> Ini sebenarnya Nama Lengkap
        // Di LoginActivity: prefs.putString("email", emailUser)

        val savedName = prefs.getString("username", "Paw User") // Ini Nama Asli dari API
        val savedEmail = prefs.getString("email", "")
        val savedHandle = prefs.getString("handle", "@user") // Handle (misal: @minpaw) buat user edit sendiri
        val profilePicPath = prefs.getString("profile_pic", null)

        // --- SETUP VIEW ---

        // Tampilkan Foto
        if (!profilePicPath.isNullOrEmpty()) {
            Glide.with(this)
                .load(Uri.parse(profilePicPath))
                .error(R.drawable.ic_profile_placeholder)
                .circleCrop()
                .into(b.imgProfile)
        } else {
            Glide.with(this)
                .load(R.drawable.ic_profile_placeholder) // Gambar default
                .circleCrop()
                .into(b.imgProfile)
        }

        // Tampilkan Data
        b.edtName.setText(savedName)

        // Karena di LoginActivity gak ada simpan "handle" (@blabla),
        // kita cek dulu, kalau handle kosong, kita isi pakai email atau default.
        if (savedHandle == "@user" && !savedEmail.isNullOrEmpty()) {
            b.edtUsername.setText(savedEmail) // Default isi pakai email kalau handle belum di-set
        } else {
            b.edtUsername.setText(savedHandle)
        }

        // Password biasanya dikosongin aja demi keamanan, user isi kalau mau ganti
        b.edtPassword.setText("")

        // --- LISTENERS ---

        // Ganti foto
        b.btnChangePic.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        // Simpan perubahan profil
        b.btnSave.setOnClickListener {
            val newName = b.edtName.text.toString().trim()
            val newHandle = b.edtUsername.text.toString().trim()
            // Password logic bisa ditambahin nanti kalau ada API update password

            val editor = prefs.edit()

            // Update data di session biar Sidebar di MainActivity juga berubah
            editor.putString("username", newName) // Update Nama
            editor.putString("handle", newHandle) // Update Handle/Username

            // Simpan gambar kalau ada perubahan (opsional, karena udah disave pas pilih gambar)
            if (selectedImageUri != null) {
                editor.putString("profile_pic", selectedImageUri.toString())
            }

            editor.apply()

            if (activity is com.example.pawpals.MainActivity) {
                (activity as com.example.pawpals.MainActivity).updateNavHeader()
            }

            Toast.makeText(requireContext(), "Profil berhasil diperbarui!", Toast.LENGTH_SHORT).show()

            // Opsional: Refresh activity biar sidebar langsung update (agak kasar tapi efektif)
            // requireActivity().recreate()
        }

        //b.btnLogout.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}