package com.csimck.hangulwidget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.createBitmap


class ColorWheelView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
    private lateinit var colorListener: ColorCallback
    private var positionX = 0.00f
    private var positionY = 0.00f
    private val paint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 5.00f
    }

    private lateinit var bitmap: Bitmap
    private val tmpCanvas = Canvas()

    companion object {
        const val TAG = "COLOR_WHEEL_VIEW"
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bitmap = createBitmap(width, height, Bitmap.Config.ARGB_8888)
        tmpCanvas.setBitmap(bitmap)
        positionX = w / 2.0f
        positionY = h / 2.0f
    }

    private var isSelecting = false
    override fun onDrawForeground(canvas: Canvas?) {
        super.onDrawForeground(canvas)
        if (isSelecting) {
            canvas?.drawLine(0.00f, positionY, width.toFloat(), positionY, paint)
            canvas?.drawLine(positionX, 0.00f, positionX, height.toFloat(), paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                updateBitmap()
                isSelecting = true
                Log.i(TAG, "down")
                invalidate()
                return true
            }
            MotionEvent.ACTION_UP -> {
                isSelecting = false
                Log.i(TAG, "up")
                performClick()
                invalidate()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                positionX = event.x
                positionY = event.y
                Log.i(TAG, "move")
                invalidate()
                updateColor()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    fun updateColor() {
        if (isWithinBounds(positionX, positionY)) {
            colorListener.colorChanged(getPixel(positionX.toInt(), positionY.toInt()))
        }
    }

    override fun performClick(): Boolean {
        Log.i(TAG, "clicked")
        return super.performClick()
    }

    private fun getPixel(x: Int, y: Int): Int {
        return bitmap.getPixel(x, y)
    }

    private fun isWithinBounds(x: Float, y: Float): Boolean {
        return x > 0 && x < height && y > 0 && y < height
    }

    fun updateBitmap() {
        if (foreground.bounds.width() > 0) {
            this.draw(tmpCanvas)
        }
    }

    interface ColorCallback {
        fun colorChanged(color: Int)
    }

    fun registerColorListener(colorCallback: ColorCallback) {
        colorListener = colorCallback
    }
}