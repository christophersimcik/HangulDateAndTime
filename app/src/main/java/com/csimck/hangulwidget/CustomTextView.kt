package com.csimck.hangulwidget

import android.graphics.*

class CustomTextView(
    initTypeface: Typeface,
    initColor: Int,
    initAlpha: Int,
    initTextSize: Float,
    initText: String,
    initVisibility: Boolean,
    initLetterSpacing: Float
) {
    var mVisibility = initVisibility
        set(value) {
            field = value
            if (this::listener.isInitialized) listener.onViewChanged()
        }
    var mTypeface = initTypeface
        set(value) {
            field = value
            paint.typeface = mTypeface
            getDimensions()
            if (this::listener.isInitialized) listener.onViewChanged()
        }
    var mColor = initColor
        set(value) {
            field = value
            paint.color = value
            if (this::listener.isInitialized) listener.onViewChanged()
        }
    var mAlpha = initAlpha
        set(value) {
            field = value
            paint.alpha = value
            if (this::listener.isInitialized) listener.onViewChanged()
        }
    var mTextSize = initTextSize
        set(value) {
            field = value
            paint.textSize = mTextSize
            getDimensions()
            if (this::listener.isInitialized) listener.onViewChanged()
        }
    var mText = initText
        set(value) {
            field = value
            getDimensions()
            if (this::listener.isInitialized) listener.onViewChanged()
        }

    var position = PointF()
    var width = 0
    var height = 0
    val paint = Paint().apply {
        color = mColor
        alpha = mAlpha
        typeface = mTypeface
        textSize = mTextSize
        textAlign = Paint.Align.CENTER
    }

    var mSpacing = initLetterSpacing
        set(value) {
            field = value
            paint.letterSpacing = value
            getDimensions()
            if (this::listener.isInitialized) listener.onViewChanged()
        }

    lateinit var listener: OnViewChangedListener

    init {
        getDimensions()
    }

    interface OnViewChangedListener {
        fun onViewChanged()
    }

    private fun getDimensions() {
        val bounds = Rect()
        paint.getTextBounds(mText, 0, mText.length, bounds)
        width = bounds.width()
        height = bounds.height()
    }
}