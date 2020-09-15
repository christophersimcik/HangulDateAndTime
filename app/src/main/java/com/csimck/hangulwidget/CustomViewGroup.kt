package com.csimck.hangulwidget

import android.graphics.*
import android.util.Size
import androidx.core.graphics.createBitmap
import kotlin.collections.ArrayList

const val MAX_ALPHA = 255

class CustomViewGroup(
    initialSize: Size,
    initialColor: Int,
    initialAlpha: Int,
    initLineSpacing: Int,
    initsidePadding: Int

) : CustomTextView.OnViewChangedListener {

    var bitmap = createBitmap(initialSize.width, initialSize.height, Bitmap.Config.ARGB_8888)
    lateinit var listener: OnBitmapChangedListener
    private var canvas = Canvas(bitmap)
    private var contentHeight = 0
    var contentWidth = 0
    private var widthPlusPadding = 0
    var widestString = ""
    var listOfViews = ArrayList<CustomTextView>()
    private val paint = Paint().apply {
        color = initialColor
        alpha = initialAlpha
    }

    var mSidePadding: Int = initsidePadding
        set(value) {
            field = value
            getTotal()
            setViewPositions()
            drawToBitmap()
        }

    var mPadding: Int = initLineSpacing
        set(value) {
            field = value
            getTotal()
            setViewPositions()
            drawToBitmap()
        }

    var mColor: Int = initialColor
        set(value) {
            field = value
            paint.color = mColor
            drawToBitmap()
        }

    var mAlpha: Int = initialAlpha
        set(value) {
            field = value
            paint.alpha = mAlpha
            drawToBitmap()
        }

    fun addView(customeView: CustomTextView) {
        listOfViews.add(customeView)
        getTotal()
        setViewPositions()
    }

    private var size: Size = initialSize
        set(value) {
            field = value
            if (isNotZero(value)) {
                updateBitmap()
            }
        }

    fun drawToBitmap() {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        canvas.drawPaint(paint)
        listOfViews.forEach {
            if (it.mVisibility) canvas.drawText(it.mText, it.position.x, it.position.y, it.paint)
        }
        if (this::listener.isInitialized) listener.onBitmapChanged(bitmap)
    }

    private fun updateBitmap() {
        bitmap.recycle()
        bitmap = createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap)
    }

    private fun setViewPositions() {
        val centerX = size.width.halfAsFloat()
        val centerY = size.height.halfAsFloat()
        var currentPosition = centerY - contentHeight / 2
        currentPosition += mPadding
        listOfViews.forEach {
            if (it.mVisibility) {
                it.position.x = centerX
                it.position.y = currentPosition + it.height
                currentPosition += it.height + mPadding
            }
        }
    }

    private fun getTotal() {
        var addPadding = 1
        contentHeight = 0
        contentWidth = 0
        listOfViews.forEach {
            if (it.mVisibility) {
                val width = it.width
                val height = it.height
                contentHeight += height
                getWidestView(width)
                addPadding++
                if (width >= contentWidth){
                    widestString = it.mText
                    contentWidth = width
                }
            }
        }
        contentHeight += addPadding * mPadding
        widthPlusPadding = contentWidth + mSidePadding.doubled()
        adjustHeight()
        adjustWidth()
    }

    override fun onViewChanged() {
        getTotal()
        setViewPositions()
        drawToBitmap()
    }

    private fun adjustHeight() {
        size = Size(size.width, contentHeight + mPadding)
    }

    private fun adjustWidth() {
        size = Size(contentWidth + (mSidePadding * 2), size.height)
    }

    interface OnBitmapChangedListener {
        fun onBitmapChanged(bitmap: Bitmap)
    }

    private fun getWidestView(width: Int) {
        if (width >= contentWidth) {
            contentWidth = width
        }
    }

    private fun isNotZero(size: Size): Boolean {
        return size.width > 0 && size.height > 0
    }
}