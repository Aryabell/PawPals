package com.example.pawpals.model

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.constraintlayout.widget.Group
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.pawpals.R

class ModelFragment : Fragment() {

    // UI Components
    private lateinit var imgPreview: ImageView
    private lateinit var btnMainAction: ImageButton
    private lateinit var btnGallery: ImageButton
    private lateinit var progressBar: ProgressBar

    // Result UI
    private lateinit var layoutResult: ConstraintLayout
    private lateinit var groupScanControls: Group
    private lateinit var tvResultBody: TextView
    private lateinit var tvConfidence: TextView
    // Launchers
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var takePhotoLauncher: ActivityResultLauncher<Intent>

    private var selectedImage: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Setup Gallery Launcher
        pickImageLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val uri: Uri? = data?.data
                uri?.let {
                    val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, it)
                    showImagePreview(bitmap)
                }
            }
        }

        // 2. Setup Camera Launcher
        takePhotoLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val bitmap = data?.extras?.get("data") as? Bitmap
                bitmap?.let {
                    showImagePreview(it)
                    // Opsional: Langsung auto-scan kalau abis foto
                    runDetection()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_model, container, false)

        // Init Views
        imgPreview = view.findViewById(R.id.imgPreview)
        btnMainAction = view.findViewById(R.id.btnMainAction)
        btnGallery = view.findViewById(R.id.btnGallery)

        progressBar = view.findViewById(R.id.progressBar)

        layoutResult = view.findViewById(R.id.layoutResult)
        groupScanControls = view.findViewById(R.id.groupScanControls)
        tvResultBody = view.findViewById(R.id.tvResultBody)
        tvConfidence = view.findViewById(R.id.tvConfidence)


        // --- Logic Tombol ---


        // Tombol Gallery
        btnGallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        // Tombol Utama (Scan / Foto)
        btnMainAction.setOnClickListener {
            if (selectedImage == null) {
                // Kalau belum ada gambar, jadi tombol kamera
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                takePhotoLauncher.launch(intent)
            } else {
                // Kalau sudah ada gambar, jadi tombol scan
                runDetection()
            }
        }

        return view
    }

    // Fungsi helper buat nampilin gambar & ubah state UI
    private fun showImagePreview(bitmap: Bitmap) {
        selectedImage = bitmap
        imgPreview.setImageBitmap(bitmap)
        // Pastikan controls masih tampil, result sembunyi
        layoutResult.visibility = View.GONE
        groupScanControls.visibility = View.VISIBLE
    }

    private fun resetUI() {
        selectedImage = null
        imgPreview.setImageResource(0) // Kosongkan atau set ke placeholder
        layoutResult.visibility = View.GONE
        groupScanControls.visibility = View.VISIBLE
    }

    private fun runDetection() {
        progressBar.visibility = View.VISIBLE
        btnMainAction.visibility = View.INVISIBLE // Sembunyiin tombol pas loading

        // Simulasi Loading 2 detik
        imgPreview.postDelayed({
            progressBar.visibility = View.GONE
            btnMainAction.visibility = View.VISIBLE

            showResult(isHealthy = true) // Coba ganti false buat tes tampilan sakit
        }, 2000)
    }

    private fun showResult(isHealthy: Boolean) {
        // Sembunyikan tombol scan & gallery
        groupScanControls.visibility = View.GONE
        // Tampilkan Modal Result
        layoutResult.visibility = View.VISIBLE


        if (isHealthy) {
            // Style Sehat (Hijau)
            tvResultBody.text = "Tidak ditemukan tanda-tanda penyakit kulit pada foto ini. Si Anabul terlihat sehat!"
            tvResultBody.setTextColor(Color.parseColor("#1B5E20")) // Hijau tua
            // Ganti warna background box (harus ambil drawable dulu biar rounded corner tetep ada)
            tvResultBody.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_confidence_badge)
            tvResultBody.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.bg_result_healthy)
        } else {
            // Style Sakit (Merah)
            tvResultBody.text = "Terdeteksi indikasi jamur kulit ringan. Segera periksa ke dokter."
            tvResultBody.setTextColor(Color.parseColor("#B71C1C")) // Merah tua
            tvResultBody.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_confidence_badge)
            tvResultBody.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.bg_result_sick)
        }
    }
}