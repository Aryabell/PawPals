package com.example.pawpals.model

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.pawpals.R

class ModelFragment : Fragment() {

    private lateinit var imgPreview: ImageView
    private lateinit var btnChooseImage: Button
    private lateinit var btnTakePhoto: Button
    private lateinit var btnDetect: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var txtResult: TextView

    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var takePhotoLauncher: ActivityResultLauncher<Intent>

    private var selectedImage: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Launcher untuk ambil dari galeri
        pickImageLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val uri: Uri? = data?.data
                uri?.let {
                    val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, it)
                    imgPreview.setImageBitmap(bitmap)
                    selectedImage = bitmap
                }
            }
        }

        // Launcher untuk ambil dari kamera
        takePhotoLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val bitmap = data?.extras?.get("data") as? Bitmap
                bitmap?.let {
                    imgPreview.setImageBitmap(it)
                    selectedImage = it
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_model, container, false)

        imgPreview = view.findViewById(R.id.imgPreview)
        btnChooseImage = view.findViewById(R.id.btnChooseImage)
        btnTakePhoto = view.findViewById(R.id.btnTakePhoto)
        btnDetect = view.findViewById(R.id.btnDetect)
        progressBar = view.findViewById(R.id.progressBar)
        txtResult = view.findViewById(R.id.txtResult)

        // Event pilih dari galeri
        btnChooseImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        // Event ambil foto kamera
        btnTakePhoto.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePhotoLauncher.launch(intent)
        }

        // Event deteksi penyakit
        btnDetect.setOnClickListener {
            if (selectedImage == null) {
                Toast.makeText(requireContext(), "Pilih gambar dulu!", Toast.LENGTH_SHORT).show()
            } else {
                runDetection()
            }
        }

        return view
    }

    private fun runDetection() {
        progressBar.visibility = View.VISIBLE
        txtResult.text = ""

        // TODO: Panggil model TensorFlow Lite / API
        // Sekarang dummy hasil dulu
        imgPreview.postDelayed({
            progressBar.visibility = View.GONE
            txtResult.text = "Hasil Deteksi: Kulit Anjing Sehat ✅"
        }, 2000) // delay 2 detik buat simulasi loading
    }
}