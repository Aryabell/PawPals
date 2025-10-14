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
import com.example.pawpals.api.ApiClient
import com.example.pawpals.databinding.FragmentProfileBinding
import com.example.pawpals.admin.LoginActivity
import com.example.pawpals.model.ResponseModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var _b: FragmentProfileBinding? = null
    private val b get() = _b!!

    private var selectedImageUri: Uri? = null

    // ✅ Ganti startActivityForResult dengan cara baru
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                selectedImageUri = result.data?.data
                b.imgProfile.setImageURI(selectedImageUri)

                // simpan sementara ke prefs
                val prefs = requireContext().getSharedPreferences("user_prefs", Activity.MODE_PRIVATE)
                prefs.edit().putString("profile_pic", selectedImageUri.toString()).apply()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _b = FragmentProfileBinding.bind(view)

        val prefs = requireContext().getSharedPreferences("user_prefs", Activity.MODE_PRIVATE)
        val profilePicPath = prefs.getString("profile_pic", null)

        // tampilkan foto profil
        if (!profilePicPath.isNullOrEmpty()) {
            if (profilePicPath.startsWith("content://") || profilePicPath.startsWith("file://")) {
                b.imgProfile.setImageURI(Uri.parse(profilePicPath))
            } else {
                val imageUrl = ApiClient.BASE_URL + profilePicPath
                Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .error(R.drawable.ic_profile_placeholder)
                    .circleCrop()
                    .into(b.imgProfile)
            }
        }

        // ganti foto
        b.btnChangePic.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        // simpan update profile
        b.btnSave.setOnClickListener {
            updateProfile()
        }

        // logout
        b.btnLogout.setOnClickListener {
            prefs.edit().clear().apply()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun updateProfile() {
        val name = b.edtName.text.toString().trim()
        val password = b.edtPassword.text.toString().trim()

        val prefs = requireContext().getSharedPreferences("user_prefs", Activity.MODE_PRIVATE)
        val id = prefs.getString("USER_ID", null)

        if (id.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "User ID tidak ditemukan, silakan login ulang.", Toast.LENGTH_SHORT).show()
            return
        }

        val idPart = id.toRequestBody("text/plain".toMediaTypeOrNull())
        val namePart = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val passPart = password.toRequestBody("text/plain".toMediaTypeOrNull())

        var profilePicPart: MultipartBody.Part? = null
        selectedImageUri?.let {
            val file = File(requireContext().cacheDir, "profile_pic.jpg")
            requireContext().contentResolver.openInputStream(it).use { input ->
                file.outputStream().use { output -> input?.copyTo(output) }
            }
            val reqFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            profilePicPart = MultipartBody.Part.createFormData("profile_pic", file.name, reqFile)
        }

        ApiClient.instance.updateProfile(idPart, namePart, passPart, profilePicPart)
            .enqueue(object : Callback<ResponseModel> {
                override fun onResponse(
                    call: Call<ResponseModel>,
                    response: Response<ResponseModel>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        Toast.makeText(requireContext(), body?.message ?: "Update berhasil", Toast.LENGTH_SHORT).show()

                        // simpan path foto profil baru ke prefs
                        if (!body?.user?.profile_pic.isNullOrEmpty()) {
                            prefs.edit().putString("profile_pic", body.user!!.profile_pic).apply()
                        } else if (selectedImageUri != null) {
                            prefs.edit().putString("profile_pic", selectedImageUri.toString()).apply()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Update gagal", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
