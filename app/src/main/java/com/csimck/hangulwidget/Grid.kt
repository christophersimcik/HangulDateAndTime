package com.csimck.hangulwidget

import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.Size

class Grid : Drawable() {
    private val paint = Paint().apply {
        color = Color.parseColor(COLOR)
    }

    companion object {
        const val GRID_SIZE = 10.0f
        const val COLOR = "#f0f0f0"
    }

    private lateinit var rects: Array<RectF>
    override fun draw(canvas: Canvas) {
        val size = Size(bounds.width(), bounds.height())
        rects = getRects(size, getPositions(size)).also { it ->
            it.forEach { rect ->
                canvas.drawRect(rect, paint)
            }
        }
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    private fun getRects(size: Size, positions: Array<PointF>): Array<RectF> {
        val sizeOfSquare = size.width.coerceAtMost(size.height).toFloat() / GRID_SIZE
        val half = sizeOfSquare.half()
        val rects: MutableList<RectF> = ArrayList()

        positions.forEach { pos ->
            val rect = RectF(
                pos.x - half,
                pos.y - half,
                pos.x + half,
                pos.y + half
            )
            rects.add(rect)
        }
        return rects.toTypedArray()
    }

    private fun getPositions(size: Size): Array<PointF> {
        val sizeOfSquare = size.width.coerceAtMost(size.height).toFloat() / GRID_SIZE
        val half = sizeOfSquare.half()
        val lateralQuantity = (size.width / sizeOfSquare).toInt()
        val longitudinalQuantity = (size.height / sizeOfSquare).toInt()
        val positions: MutableList<PointF> = ArrayList()
        var row = half
        var col = half
        var canDraw: Boolean
        for (i in 0 until longitudinalQuantity) {
            for (j in 0 until lateralQuantity) {
                canDraw = determineIfCheckered(i, j)
                if (canDraw) {
                    val position = PointF(row, col)
                    positions.add(position)
                }
                row += sizeOfSquare
            }
            row = half
            col += sizeOfSquare
        }
        return positions.toTypedArray()
    }

    private fun determineIfCheckered(i: Int, j: Int): Boolean {
        return if (i % 2 == 0) {
            j % 2 == 0
        } else {
            j % 2 != 0
        }
    }
}