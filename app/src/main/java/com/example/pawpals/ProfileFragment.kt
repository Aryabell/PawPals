package com.example.pawpals.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.pawpals.R
import com.example.pawpals.api.ApiClient
import com.example.pawpals.databinding.FragmentProfileBinding
import com.example.pawpals.model.ResponseModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var _b: FragmentProfileBinding? = null
    private val b get() = _b!!

    private var selectedImageUri: Uri? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _b = FragmentProfileBinding.bind(view)

        b.btnChangePic.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 100)
        }

        b.btnSave.setOnClickListener {
            updateProfile()
        }
    }

    private fun updateProfile() {
        val name = b.edtName.text.toString().trim()
        val password = b.edtPassword.text.toString().trim()

        // 🔹 Ambil ID user dari SharedPreferences
        val sharedPref = requireContext().getSharedPreferences("user_session", Activity.MODE_PRIVATE)
        val prefs = requireContext().getSharedPreferences("user_prefs", Activity.MODE_PRIVATE)
        val id = prefs.getString("USER_ID", null)


        if (id.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "User ID tidak ditemukan, silakan login ulang.", Toast.LENGTH_SHORT).show()
            return
        }

        val idPart = RequestBody.create("text/plain".toMediaTypeOrNull(), id)
        val namePart = RequestBody.create("text/plain".toMediaTypeOrNull(), name)
        val passPart = RequestBody.create("text/plain".toMediaTypeOrNull(), password)

        var profilePicPart: MultipartBody.Part? = null
        selectedImageUri?.let {
            val file = File(requireContext().cacheDir, "profile_pic.jpg")
            requireContext().contentResolver.openInputStream(it).use { input ->
                file.outputStream().use { output -> input?.copyTo(output) }
            }
            val reqFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            profilePicPart = MultipartBody.Part.createFormData("profile_pic", file.name, reqFile)
        }

        ApiClient.instance.updateProfile(idPart, namePart, passPart, profilePicPart)
            .enqueue(object : Callback<ResponseModel> {
                override fun onResponse(
                    call: Call<ResponseModel>,
                    response: Response<ResponseModel>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), response.body()?.message, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Update gagal", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            b.imgProfile.setImageURI(selectedImageUri)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}