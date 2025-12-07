package com.example.pawpals.adoption

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.pawpals.api.AdoptionClient
import com.example.pawpals.api.DogResponse
import com.example.pawpals.databinding.ActivityAdoptionOwnerFormBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AdoptionOwnerFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdoptionOwnerFormBinding
    private var imageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imageUri = result.data?.data
            binding.ivPreview.setImageURI(imageUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdoptionOwnerFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Tambah Hewan Adopsi"

        binding.btnUploadImage.setOnClickListener { pickImage() }
        binding.btnSubmit.setOnClickListener { submitDog() }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun submitDog() {

        val name = binding.etDogName.text.toString().trim()
        val breed = binding.etBreed.text.toString().trim()
        val location = binding.etLocation.text.toString().trim()
        val age = binding.etAge.text.toString().trim()
        val weight = binding.etWeight.text.toString().trim()

        val ownerName = binding.etOwnerName.text.toString().trim()
        val ownerPhone = binding.etOwnerPhone.text.toString().trim()
        val ownerHandle = binding.etOwnerHandle.text.toString().trim()

        // VALIDASI
        if (name.isEmpty() || breed.isEmpty() || location.isEmpty() ||
            age.isEmpty() || weight.isEmpty() ||
            ownerName.isEmpty() || ownerPhone.isEmpty() || ownerHandle.isEmpty()
        ) {
            Toast.makeText(this, "Isi semua data!", Toast.LENGTH_SHORT).show()
            return
        }

        if (imageUri == null) {
            Toast.makeText(this, "Pilih foto dulu!", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnSubmit.isEnabled = false

        // KONVERSI GAMBAR
        val file = uriToFile(imageUri!!)
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val photoPart = MultipartBody.Part.createFormData("image", file.name, requestFile)

        // OWNER ID DARI LOGIN SESSION
        val ownerIdValue = getSharedPreferences("user", MODE_PRIVATE)
            .getInt("user_id", 0)

        val ownerIdBody: RequestBody =
            ownerIdValue.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        val call = AdoptionClient.instance.addDog(
            name.toRequestBody("text/plain".toMediaTypeOrNull()),
            breed.toRequestBody("text/plain".toMediaTypeOrNull()),
            location.toRequestBody("text/plain".toMediaTypeOrNull()),
            age.toRequestBody("text/plain".toMediaTypeOrNull()),
            weight.toRequestBody("text/plain".toMediaTypeOrNull()),

            ownerIdBody,   // ← FIXED (sebelumnya error ownerId)

            ownerName.toRequestBody("text/plain".toMediaTypeOrNull()),
            ownerPhone.toRequestBody("text/plain".toMediaTypeOrNull()),
            ownerHandle.toRequestBody("text/plain".toMediaTypeOrNull()),

            photoPart
        )

        call.enqueue(object : Callback<DogResponse> {
            override fun onResponse(call: Call<DogResponse>, response: Response<DogResponse>) {
                binding.btnSubmit.isEnabled = true

                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(this@AdoptionOwnerFormActivity, "Berhasil ditambahkan!", Toast.LENGTH_LONG).show()
                    setResult(RESULT_OK)
                    finish()
                } else {
                    Toast.makeText(
                        this@AdoptionOwnerFormActivity,
                        "Gagal: ${response.body()?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<DogResponse>, t: Throwable) {
                binding.btnSubmit.isEnabled = true
                Toast.makeText(this@AdoptionOwnerFormActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun uriToFile(uri: Uri): File {
        val contentResolver = contentResolver
        val file = File(cacheDir, "upload_${System.currentTimeMillis()}.jpg")

        val inputStream = contentResolver.openInputStream(uri)
        val outputStream = file.outputStream()

        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        return file
    }
}
