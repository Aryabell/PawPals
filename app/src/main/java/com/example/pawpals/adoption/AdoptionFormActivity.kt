package com.example.pawpals.adoption

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.pawpals.api.AdoptionClient
import com.example.pawpals.api.AdoptionResponse
import com.example.pawpals.databinding.ActivityAdoptionFormBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
            val f = uriToFile(it, this)
            if (f != null) {
                imageFile = f
                binding.ivPreview.setImageURI(it)
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
        binding.toolbarForm.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val dogName = intent.getStringExtra("dogName") ?: "Dog"
        val dogId = intent.getIntExtra("dogId", -1)
        val userId = "USER123" // ganti dari session atau preferencemu

        binding.btnPickImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnSubmit.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val address = binding.etAddress.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val reason = binding.etReason.text.toString().trim()
            val agree = binding.cbAgree.isChecked

            if (name.isEmpty() || address.isEmpty() || phone.isEmpty() || reason.isEmpty()) {
                Toast.makeText(this, "Semua kolom wajib diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!agree) {
                Toast.makeText(this, "Anda harus menyetujui persyaratan adopsi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Build RequestBody
            val rbUserId = userId.toRequestBody("text/plain".toMediaTypeOrNull())
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

            // Call API
            val api = AdoptionClient.instance
            val call = api.submitAdoption(
                rbUserId, rbDogId, rbDogName,
                rbName, rbAddress, rbPhone, rbReason, imagePart
            )

            // Optional: disable form / show progress
            binding.btnSubmit.isEnabled = false

            call.enqueue(object : Callback<AdoptionResponse> {
                override fun onResponse(call: Call<AdoptionResponse>, response: Response<AdoptionResponse>) {
                    binding.btnSubmit.isEnabled = true
                    if (response.isSuccessful && response.body()?.status == "success") {
                        Toast.makeText(this@AdoptionFormActivity, "Pengajuan adopsi terkirim!", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        val msg = response.body()?.message ?: "Response error: ${response.code()}"
                        Toast.makeText(this@AdoptionFormActivity, "Gagal: $msg", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<AdoptionResponse>, t: Throwable) {
                    binding.btnSubmit.isEnabled = true
                    Toast.makeText(this@AdoptionFormActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    // Convert content:// uri to File in cache
    private fun uriToFile(uri: Uri, context: Context): File? {
        val resolver: ContentResolver = context.contentResolver
        var inputStream: InputStream? = null
        var output: FileOutputStream? = null
        return try {
            val fileName = queryName(resolver, uri) ?: "temp_image_${System.currentTimeMillis()}.jpg"
            val tmpFile = File(context.cacheDir, fileName)
            inputStream = resolver.openInputStream(uri)
            output = FileOutputStream(tmpFile)
            inputStream?.copyTo(output)
            tmpFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            inputStream?.close()
            output?.close()
        }
    }

    private fun queryName(resolver: ContentResolver, uri: Uri): String? {
        val returnCursor = resolver.query(uri, null, null, null, null) ?: return null
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name = if (nameIndex >= 0) returnCursor.getString(nameIndex) else null
        returnCursor.close()
        return name
    }
}
