package com.example.pawpals

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pawpals.api.ApiClient
import com.example.pawpals.databinding.ActivityRegisterBinding
import com.example.pawpals.model.ResponseModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Tombol pilih gambar
        binding.btnSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
            startActivityForResult(intent, 100)
        }

        // Tombol register
        binding.btnRegister.setOnClickListener { registerUser() }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            // kembali ke LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @Deprecated("onActivityResult is deprecated, but fine for simple use here")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.data
            binding.imageViewProfile.setImageURI(selectedImageUri)
        }
    }

    private fun registerUser() {
        val name = binding.edtName.text.toString().trim()
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val namePart = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val emailPart = email.toRequestBody("text/plain".toMediaTypeOrNull())
        val passwordPart = password.toRequestBody("text/plain".toMediaTypeOrNull())
        val rolePart = "user".toRequestBody("text/plain".toMediaTypeOrNull())

        var profilePart: MultipartBody.Part? = null
        selectedImageUri?.let { uri ->
            val file = uriToFile(uri)
            val reqFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            profilePart = MultipartBody.Part.createFormData("profile_pic", file.name, reqFile)
        }

        val api = ApiClient.instance
        api.register(namePart, emailPart, passwordPart, rolePart, profilePart)
            .enqueue(object : Callback<ResponseModel> {
                override fun onResponse(
                    call: Call<ResponseModel>,
                    response: Response<ResponseModel>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        Toast.makeText(
                            this@RegisterActivity,
                            response.body()!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Gagal: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                    Toast.makeText(this@RegisterActivity, t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun uriToFile(uri: Uri): File {
        val fileName = getFileName(uri) ?: "temp_image"
        val tempFile = File(cacheDir, fileName)
        contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }
        return tempFile
    }

    private fun getFileName(uri: Uri): String? {
        var name: String? = null
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex >= 0) {
                name = cursor.getString(nameIndex)
            }
        }
        return name
    }
}
