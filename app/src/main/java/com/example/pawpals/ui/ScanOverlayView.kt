package com.example.pawpals.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class ScanOverlayView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val eraser = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    // --- ATUR DISINI ---
    // Radius kelengkungan (makin gede makin bunder/tumpul)
    private val cornerRadius = 40f * resources.displayMetrics.density

    // Ukuran kotak scan (harus sama kayak di XML: 280dp)
    private val boxSize = 280f * resources.displayMetrics.density
    // -------------------

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 1. Bikin Layer Komposit (Penting biar eraser jalan)
        val layerId = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)

        // 2. Gambar Background Gelap Full Screen
        canvas.drawColor(Color.parseColor("#99000000"))

        // 3. Hitung Posisi Tengah (Sesuai Logic Vertical Bias 0.4 di XML)
        // Rumus Bias: y = (TinggiLayar - TinggiKotak) * bias
        val top = (height - boxSize) * 0.4f
        val left = (width - boxSize) / 2f

        // 4. Hapus Bagian Tengah (Bikin Bolong Rounded)
        canvas.drawRoundRect(
            left, top, left + boxSize, top + boxSize,
            cornerRadius, cornerRadius,
            eraser
        )

        canvas.restoreToCount(layerId)
    }
}