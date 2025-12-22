package com.example.pawpals.adoption

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.example.pawpals.api.AdoptionClient
import com.example.pawpals.api.AdoptionResponse
import com.example.pawpals.databinding.ActivityAdoptionFormBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class AdoptionFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdoptionFormBinding
    private var imageFile: File? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val file = uriToFile(it, this)
            if (file != null) {
                imageFile = file

                // ✅ Preview image pakai Coil (ANTI HITAM)
                binding.ivPreview.load(it) {
                    crossfade(true)
                    error(android.R.drawable.ic_menu_report_image)
                }
            } else {
                Toast.makeText(this, "Gagal membaca file", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdoptionFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar
        setSupportActionBar(binding.toolbarForm)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Paw Adopsi"
        binding.toolbarForm.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val dogName = intent.getStringExtra("dogName") ?: "Dog"
        val dogId = intent.getIntExtra("dogId", -1)

        binding.btnPickImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnSubmit.setOnClickListener {
            submitForm(dogId, dogName)
        }
    }

    private fun submitForm(dogId: Int, dogName: String) {
        val name = binding.etName.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val reason = binding.etReason.text.toString().trim()
        val agree = binding.cbAgree.isChecked

        if (name.isEmpty() || address.isEmpty() || phone.isEmpty() || reason.isEmpty()) {
            Toast.makeText(this, "Semua kolom wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }

        if (!agree) {
            Toast.makeText(this, "Anda harus menyetujui persyaratan adopsi", Toast.LENGTH_SHORT).show()
            return
        }

        // Disable UI + show loading
        binding.btnSubmit.isEnabled = false
        binding.loadingOverlay.visibility = View.VISIBLE

        val rbDogId = dogId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val rbDogName = dogName.toRequestBody("text/plain".toMediaTypeOrNull())
        val rbName = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val rbAddress = address.toRequestBody("text/plain".toMediaTypeOrNull())
        val rbPhone = phone.toRequestBody("text/plain".toMediaTypeOrNull())
        val rbReason = reason.toRequestBody("text/plain".toMediaTypeOrNull())

        val imagePart: MultipartBody.Part? = imageFile?.let { file ->
            val reqFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("image", file.name, reqFile)
        }

        val call = AdoptionClient.instance.submitAdoption(
            rbDogId,
            rbDogName,
            rbName,
            rbAddress,
            rbPhone,
            rbReason,
            imagePart
        )

        call.enqueue(object : Callback<AdoptionResponse> {
            override fun onResponse(
                call: Call<AdoptionResponse>,
                response: Response<AdoptionResponse>
            ) {
                binding.btnSubmit.isEnabled = true
                binding.loadingOverlay.visibility = View.GONE
                binding.btnSubmit.isEnabled = true

                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(
                        this@AdoptionFormActivity,
                        "Pengajuan adopsi terkirim!",
                        Toast.LENGTH_LONG
                    ).show()

                    // ✅ KIRIM RESULT → ANTI BLACK SCREEN
                    setResult(RESULT_OK)
                    finish()
                } else {
                    val msg = response.body()?.message ?: "Error ${response.code()}"
                    Toast.makeText(this@AdoptionFormActivity, msg, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<AdoptionResponse>, t: Throwable) {
                binding.loadingOverlay.visibility = View.GONE
                binding.btnSubmit.isEnabled = true
                Toast.makeText(this@AdoptionFormActivity, t.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    // ================= FILE UTILS =================

    private fun uriToFile(uri: Uri, context: Context): File? {
        val resolver: ContentResolver = context.contentResolver
        return try {
            val fileName = queryName(resolver, uri)
                ?: "image_${System.currentTimeMillis()}.jpg"
            val tempFile = File(context.cacheDir, fileName)
            resolver.openInputStream(uri).use { input ->
                FileOutputStream(tempFile).use { output ->
                    input?.copyTo(output)
                }
            }
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun queryName(resolver: ContentResolver, uri: Uri): String? {
        val cursor = resolver.query(uri, null, null, null, null) ?: return null
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        val name = if (nameIndex >= 0) cursor.getString(nameIndex) else null
        cursor.close()
        return name
    }
}
