package com.example.pawpals.admin.fragments

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
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.pawpals.R

class ModelFragment : Fragment() {


    private lateinit var imgPreview: ImageView
    private lateinit var btnMainAction: ImageButton
    private lateinit var btnGallery: ImageButton
    private lateinit var progressBar: ProgressBar

    private lateinit var layoutResult: ConstraintLayout
    private lateinit var groupScanControls: Group
    private lateinit var tvResultBody: TextView
    private lateinit var tvConfidence: TextView

    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var takePhotoLauncher: ActivityResultLauncher<Intent>

    private var selectedImage: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        takePhotoLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val bitmap = data?.extras?.get("data") as? Bitmap
                bitmap?.let {
                    showImagePreview(it)
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


        imgPreview = view.findViewById(R.id.imgPreview)
        btnMainAction = view.findViewById(R.id.btnMainAction)
        btnGallery = view.findViewById(R.id.btnGallery)

        progressBar = view.findViewById(R.id.progressBar)

        layoutResult = view.findViewById(R.id.layoutResult)
        groupScanControls = view.findViewById(R.id.groupScanControls)
        tvResultBody = view.findViewById(R.id.tvResultBody)
        tvConfidence = view.findViewById(R.id.tvConfidence)

        btnGallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        btnMainAction.setOnClickListener {
            if (selectedImage == null) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                takePhotoLauncher.launch(intent)
            } else {
                runDetection()
            }
        }

        return view
    }

    private fun showImagePreview(bitmap: Bitmap) {
        selectedImage = bitmap
        imgPreview.setImageBitmap(bitmap)

        layoutResult.visibility = View.GONE
        groupScanControls.visibility = View.VISIBLE
    }

    private fun resetUI() {
        selectedImage = null
        imgPreview.setImageResource(0)
        layoutResult.visibility = View.GONE
        groupScanControls.visibility = View.VISIBLE
    }

    private fun runDetection() {
        progressBar.visibility = View.VISIBLE
        btnMainAction.visibility = View.INVISIBLE

        imgPreview.postDelayed({
            progressBar.visibility = View.GONE
            btnMainAction.visibility = View.VISIBLE

            showResult(isHealthy = true)
        }, 2000)
    }

    private fun showResult(isHealthy: Boolean) {
        groupScanControls.visibility = View.GONE
        layoutResult.visibility = View.VISIBLE


        if (isHealthy) {
            tvResultBody.text = "Tidak ditemukan tanda-tanda penyakit kulit pada foto ini. Si Anabul terlihat sehat!"
            tvResultBody.setTextColor(Color.parseColor("#1B5E20"))
            tvResultBody.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_confidence_badge)
            tvResultBody.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.bg_result_healthy)
        } else {
            tvResultBody.text = "Terdeteksi indikasi jamur kulit ringan. Segera periksa ke dokter."
            tvResultBody.setTextColor(Color.parseColor("#B71C1C"))
            tvResultBody.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_confidence_badge)
            tvResultBody.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.bg_result_sick)
        }
    }
}