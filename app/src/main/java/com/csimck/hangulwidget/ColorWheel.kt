package com.csimck.hangulwidget

import android.graphics.*
import android.graphics.drawable.Drawable

class ColorWheel : Drawable() {
    companion object;

    private val colors = intArrayOf(
        Color.RED,
        Color.MAGENTA,
        Color.BLUE,
        Color.CYAN,
        Color.GREEN,
        Color.YELLOW,
        Color.RED
    )


    private val colorPositions =
        floatArrayOf(0.000f, 0.166f, 0.333f, 0.499f, 0.666f, 0.833f, 0.999f)
    private lateinit var hueGradient: Shader
    private lateinit var hiliteGradient: Shader
    private lateinit var satGradient: Shader
    private lateinit var composeShader: ComposeShader
    private val huePaint = Paint()
    private val satPaint = Paint()
    private var black = Color.BLACK
    private var white = Color.WHITE
    private var width = 0
    private var height = 0
    private var radius = 1.0f

    override fun draw(canvas: Canvas) {
        width = bounds.width()
        height = bounds.height()
        radius = bounds.width().halfAsFloat()
        initializeGradients(width, height)
        setShaders()
        drawHueLayer(width, height, canvas)
        drawSatLayer(width, height, canvas)
    }

    override fun setAlpha(alpha: Int) {
        invalidateSelf()
    }

    fun setValue(alpha: Int) {
        black = adjustTransparency(Color.BLACK, alpha)
        white = adjustTransparency(Color.WHITE, alpha)
        satGradient = RadialGradient(
            width.halfAsFloat(),
            height.halfAsFloat(),
            radius,
            white,
            black,
            Shader.TileMode.CLAMP
        )
        invalidateSelf()
    }

    private fun adjustTransparency(color: Int, alpha: Int): Int {
        return color.let { color ->
            val red = Color.red(color)
            val grn = Color.green(color)
            val blu = Color.blue(color)
            Color.argb(alpha, red, grn, blu)
        }
    }

    fun setAllAlpha(alpha: Int) {
        huePaint.alpha = alpha
        satPaint.alpha = alpha
        invalidateSelf()
    }

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        huePaint.colorFilter = colorFilter
    }

    private fun drawSatLayer(width: Int, height: Int, canvas: Canvas) {
        val radius = width.halfAsFloat()
        canvas.drawCircle(
            width.halfAsFloat(),
            height.halfAsFloat(),
            radius,
            satPaint
        )
    }

    private fun drawHueLayer(width: Int, height: Int, canvas: Canvas) {
        val radius = width.halfAsFloat()
        canvas.drawCircle(
            width.halfAsFloat(),
            height.halfAsFloat(),
            radius,
            huePaint
        )
    }

    private fun setShaders() {
        huePaint.shader = composeShader
        satPaint.shader = satGradient
    }

    private fun initializeGradients(width: Int, height: Int) {
        val radius = width.halfAsFloat()
        hueGradient = SweepGradient(
            width.halfAsFloat(),
            height.halfAsFloat(),
            colors,
            colorPositions
        )
        hiliteGradient = RadialGradient(
            width.halfAsFloat(),
            height.halfAsFloat(),
            radius,
            Color.WHITE,
            Color.TRANSPARENT,
            Shader.TileMode.CLAMP
        )
        satGradient = RadialGradient(
            width.halfAsFloat(),
            height.halfAsFloat(),
            radius,
            white,
            black,
            Shader.TileMode.CLAMP
        )
        composeShader = ComposeShader(hiliteGradient, hueGradient, PorterDuff.Mode.DST_ATOP)
    }
}