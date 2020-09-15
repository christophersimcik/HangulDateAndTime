package com.csimck.hangulwidget

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.color_picker_dialog.*

class ColorPickerDialog : DialogFragment(), ColorWheelView.ColorCallback {
    private lateinit var colorPicker: ColorWheelView
    private lateinit var colorPreview: AppCompatImageView
    private lateinit var saturationSlider: SeekBar
    private lateinit var alphaSlider: SeekBar
    private lateinit var listener: Callback
    private lateinit var button: AppCompatButton
    private var saturation = 0
    private var transparency = 0
    private var color = 0
    private val repository = Repository.instance
    var type = ""

    companion object {
        const val TAG = "ColorPickerDialog"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.color_picker_dialog, container, false)
        saturationSlider = view.findViewById<SeekBar>(R.id.saturation_slider).apply {
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                    Log.i(TAG, "saturation progress changed")
                    val colorwheel = colorPicker.foreground as ColorWheel
                    colorwheel.setValue((progress - 255) * -1)
                    colorPicker.updateBitmap()
                    colorPicker.updateColor()
                    saturation = progress
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                    Log.i(TAG, "saturation start tracking")
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    Log.i(TAG, "saturation stop tracking")
                }

            })
        }

        alphaSlider = view.findViewById<SeekBar>(R.id.alpha_slider).apply {
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                    Log.i(TAG, "alpha progress changed")
                    val colorwheel = colorPicker.foreground as ColorWheel
                    colorwheel.setAllAlpha(progress)
                    colorPicker.updateBitmap()
                    colorPicker.updateColor()
                    transparency = progress

                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                    Log.i(TAG, "alpha start tracking")
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    Log.i(TAG, "alpha stop tracking")
                }

            })
        }

        val size = (getSize().width.coerceAtMost(getSize().height))
        colorPicker = view.findViewById<ColorWheelView>(R.id.color_select_wheel).apply {
            val params = layoutParams
            params.width = size
            params.height = size
            layoutParams = params
            background = Grid()
            foreground = ColorWheel()
        }
        colorPreview = view.findViewById<AppCompatImageView>(R.id.color_select_preview).apply {
            val params = layoutParams
            params.width = size / 2
            params.height = size / 2
            layoutParams = params
            background = Grid()
        }
        colorPicker.registerColorListener(this)

        button = view.findViewById<AppCompatButton>(R.id.button).apply {
            setOnClickListener {
                // *IMPORTANT order of array is C(olor), A(lpha), S(aturation)
                listener.onColorSelect(type, intArrayOf(color, transparency, saturation))
            }

        }
        return view
    }

    override fun onResume() {
        super.onResume()
        val displayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context?.display?.getRealMetrics(displayMetrics)
        } else {
            context?.getSystemService(Context.WINDOW_SERVICE).let {
                it as WindowManager
                it.defaultDisplay.getMetrics(displayMetrics)
            }
        }
        dialog?.window?.setLayout(
            (displayMetrics.widthPixels * .90).toInt(),
            (displayMetrics.heightPixels * .90).toInt()
        )
        getPreferences()
    }

    private fun getSize(): Size {
        val displayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context?.display?.getRealMetrics(displayMetrics)
        } else {
            context?.getSystemService(Context.WINDOW_SERVICE).let {
                it as WindowManager
                it.defaultDisplay.getMetrics(displayMetrics)
            }
        }
        return Size(
            (displayMetrics.widthPixels * .60).toInt(),
            (displayMetrics.heightPixels * .60).toInt()
        )
    }

    interface Callback {
        fun onColorSelect(type: String, values: IntArray)
    }

    fun registerListener(callback: Callback) {
        listener = callback
    }

    private fun getPreferences() {
        val sharedPreferences = context?.getSharedPreferences(SHARED_PREFERENCES, 0)
        Log.i(TAG, "prefs $type")
        when (type) {
            TIME -> {
                transparency = repository.getTimeAlpha(sharedPreferences)
                saturation = repository.getTimeSaturation(sharedPreferences)
                color = sharedPreferences?.getInt(TIME_COLOR, 0) ?: 0
                saturation_slider.progress = saturation
                alpha_slider.progress = transparency
                colorPreview.foreground = addTransparency(color, transparency).toDrawable()
            }
            DATE -> {
                transparency = repository.getDateAlpha(sharedPreferences)
                saturation = repository.getDateSaturation(sharedPreferences)
                color = sharedPreferences?.getInt(DATE_COLOR, 0) ?: 0
                saturation_slider.progress = saturation
                alpha_slider.progress = transparency
                colorPreview.foreground = addTransparency(color, transparency).toDrawable()
            }
            BACKGROUND -> {
                transparency = repository.getBackgroundAlpha(sharedPreferences)
                saturation = repository.getBackgroundSaturation(sharedPreferences)
                color = sharedPreferences?.getInt(BACKGROUND_COLOR, 0) ?: 0
                saturation_slider.progress = saturation
                alpha_slider.progress = transparency
                colorPreview.foreground = addTransparency(color, transparency).toDrawable()
            }
        }
    }

    override fun colorChanged(color: Int) {
        this.color = color
        colorPreview.foreground = addTransparency(color, transparency).toDrawable()
    }

    private fun addTransparency(color: Int, alpha: Int): Int {
        return color.let {
            val red = Color.red(color)
            val grn = Color.green(color)
            val blu = Color.blue(color)
            Color.argb(alpha, red, grn, blu)
        }
    }
}