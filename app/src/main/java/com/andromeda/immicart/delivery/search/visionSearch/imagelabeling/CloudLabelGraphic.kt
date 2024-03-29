package com.andromeda.immicart.delivery.search.visionSearch.imagelabeling

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

/** Graphic instance for rendering detected label.  */
class CloudLabelGraphic(private val overlay: GraphicOverlay, private val labels: List<String>) :
    GraphicOverlay.Graphic(overlay) {

    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 60.0f
    }

    @Synchronized
    override fun draw(canvas: Canvas) {
        val x = overlay.width / 4.0f
        var y = overlay.height / 4.0f

        for (label in labels) {
            canvas.drawText(label, x, y, textPaint)
            y -= 62.0f
        }
    }
}
