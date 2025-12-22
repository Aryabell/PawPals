package com.example.pawpals.admin.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.pawpals.R
import com.example.pawpals.api.ApiClient
// import com.example.pawpals.api.ApiClient // Sesuaikan ini!
// import com.example.pawpals.api.ApiService // Sesuaikan ini!
import com.google.gson.JsonObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ModelFragment : Fragment() {

    // UI Components
    private lateinit var viewFinder: PreviewView
    private lateinit var imgPreview: ImageView
    private lateinit var btnMainAction: ImageButton // Tombol Scan
    private lateinit var btnGallery: ImageButton
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutResult: ConstraintLayout
    private lateinit var groupScanControls: Group
    private lateinit var tvResultBody: TextView
    private lateinit var tvConfidence: TextView
    private lateinit var tvResultTitle: TextView

    // CameraX
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    // Setup API URL (Ganti ini tiap Ngrok restart!)
    private val NGROK_URL = "https://ganti-pake-link-ngrok-lu.ngrok-free.app/predict"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_model, container, false)

        // Init UI
        viewFinder = view.findViewById(R.id.viewFinder)
        imgPreview = view.findViewById(R.id.imgPreview)
        btnMainAction = view.findViewById(R.id.btnMainAction)
        btnGallery = view.findViewById(R.id.btnGallery)
        progressBar = view.findViewById(R.id.progressBar)
        layoutResult = view.findViewById(R.id.layoutResult)
        groupScanControls = view.findViewById(R.id.groupScanControls)
        tvResultBody = view.findViewById(R.id.tvResultBody)
        tvConfidence = view.findViewById(R.id.tvConfidence)
        tvResultTitle = view.findViewById(R.id.tvResultTitle)

        cameraExecutor = Executors.newSingleThreadExecutor()

        // Cek Izin Kamera dulu
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions.launch(Manifest.permission.CAMERA)
        }

        // --- TOMBOL SCAN (JEPRET) ---
        btnMainAction.setOnClickListener {
            takePhoto()
        }

        // --- TOMBOL GALERI ---
        btnGallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        return view
    }

    // --- LOGIC 1: START KAMERA LIVE ---
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            // ImageCapture (Biar bisa dijepret)
            imageCapture = ImageCapture.Builder().build()

            // Pilih Kamera Belakang
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (exc: Exception) {
                Log.e("PawPals", "Gagal start kamera: ${exc.message}")
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    // --- LOGIC 2: JEPRET FOTO ---
    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        // Animasi Loading
        progressBar.visibility = View.VISIBLE
        btnMainAction.isEnabled = false

        // Bikin file temporary buat nyimpen hasil jepret
        val photoFile = File(
            requireContext().externalCacheDir,
            SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
                .format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(context, "Gagal jepret: ${exc.message}", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                    btnMainAction.isEnabled = true
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    // Foto berhasil diambil, tampilkan di ImageView & Kirim ke API
                    val savedUri = Uri.fromFile(photoFile)
                    val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)

                    showImagePreview(bitmap)
                    uploadImageToApi(photoFile) // <-- KIRIM KE SERVER
                }
            }
        )
    }

    // --- LOGIC 3: PILIH DARI GALERI ---
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri: Uri? = result.data?.data
            uri?.let {
                // Convert URI ke File biar bisa diupload
                val file = uriToFile(it)
                val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, it)

                showImagePreview(bitmap)
                progressBar.visibility = View.VISIBLE
                uploadImageToApi(file)
            }
        }
    }

    // --- LOGIC 4: UPLOAD KE API (CORE) ---
    private fun uploadImageToApi(file: File) {
        // 1. Definisikan URL Ngrok (GANTI INI TIAP KALI NYALAIN NGROK!)
        // Pastikan ujungnya ada "/predict" sesuai endpoint di main.py
        val ngrokUrl = "http://geochemical-jaylah-deludingly.ngrok-free.dev/predict"

        // 2. Siapin Request Body (Gambar)
        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        // 3. Panggil API (Pake ApiClient singleton lu)
        // Perhatikan parameter pertama adalah 'ngrokUrl'
        ApiClient.instance.scanPenyakit(ngrokUrl, body).enqueue(object : Callback<JsonObject> {

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                // Sembunyikan loading
                progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    val json = response.body()

                    // Ambil data dari JSON Python (sesuaikan key-nya dengan return main.py)
                    val penyakit = json?.get("penyakit")?.asString ?: "Tidak Diketahui"
                    val akurasi = json?.get("akurasi")?.asString ?: "0%"

                    // Tampilkan Hasil
                    showResultUI(penyakit, akurasi)
                } else {
                    Toast.makeText(context, "Server Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    resetUI()
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(context, "Gagal Konek: ${t.message}", Toast.LENGTH_SHORT).show()
                resetUI()
            }
        })
    }

    // --- HELPER UI ---
    private fun showResultUI(penyakit: String, akurasi: String) {
        groupScanControls.visibility = View.GONE
        layoutResult.visibility = View.VISIBLE

        tvConfidence.text = "Tingkat Keyakinan: $akurasi"

        if (penyakit.equals("Healthy", ignoreCase = true) || penyakit.equals("Sehat", ignoreCase = true)) {
            tvResultTitle.text = "Anabul Sehat!"
            tvResultBody.text = "Tidak ditemukan tanda penyakit."
            tvResultBody.setTextColor(Color.parseColor("#1B5E20")) // Hijau
            tvResultBody.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.bg_result_healthy)
        } else {
            tvResultTitle.text = "Terdeteksi: $penyakit"
            tvResultBody.text = "Segera periksa ke dokter hewan."
            tvResultBody.setTextColor(Color.parseColor("#B71C1C")) // Merah
            tvResultBody.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.bg_result_sick)
        }
    }

    private fun showImagePreview(bitmap: Bitmap) {
        imgPreview.setImageBitmap(bitmap)
        imgPreview.visibility = View.VISIBLE
        viewFinder.visibility = View.INVISIBLE // Sembunyikan kamera live
    }

    private fun resetUI() {
        imgPreview.visibility = View.GONE
        viewFinder.visibility = View.VISIBLE // Balikin kamera live
        layoutResult.visibility = View.GONE
        groupScanControls.visibility = View.VISIBLE
        btnMainAction.isEnabled = true
    }

    // --- HELPER PERMISSIONS & FILE ---
    private val requestPermissions = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) startCamera() else Toast.makeText(context, "Izin kamera ditolak", Toast.LENGTH_SHORT).show()
    }

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        requireContext(), Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    private fun uriToFile(uri: Uri): File {
        val contentResolver = requireContext().contentResolver
        val tempFile = File.createTempFile("temp_image", ".jpg", requireContext().cacheDir)
        val inputStream = contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(tempFile)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        return tempFile
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}