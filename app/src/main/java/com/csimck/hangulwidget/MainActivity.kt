package com.csimck.hangulwidget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.Size
import android.view.WindowManager
import android.widget.CompoundButton
import android.widget.RemoteViews
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.alpha
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import kotlinx.coroutines.*
import net.danlew.android.joda.JodaTimeAndroid
import kotlin.coroutines.CoroutineContext

const val DATE_BOOL = "date"
const val ENGLISH_BOOL = "english"
const val TEXT_SZ = "text_size"
const val SHARED_PREFERENCES = "shared_preferences"
const val DATE_COLOR = "date_color"
const val DATE_SATURATION = "date_saturation"
const val DATE_ALPHA = "date_alpha"
const val TIME_COLOR = "time_color"
const val TIME_SATURATION = "time_saturation"
const val TIME_ALPHA = "time_alpha"
const val BACKGROUND_COLOR = "background_color"
const val BACKGROUND_SATURATION = "background_saturation"
const val BACKGROUND_ALPHA = "background_alpha"
const val DISPLAY_WIDTH = "display_width"
const val DISPLAY_HEIGHT = "display_height"
const val LINE_SPACING = "padding"
const val LETTER_SPACING = "letter_spacing"
const val SIDE_PADDING = "side_spacing"
const val TYPE = "type"
const val DATE = "date"
const val TIME = "time"
const val FONT = "font"
const val BACKGROUND = "background"
const val COLOR = 0
const val TRANSPARENCY = 1
const val SATURATION = 2

class MainActivity : AppCompatActivity(), ColorPickerDialog.Callback,
    CustomViewGroup.OnBitmapChangedListener {
    private lateinit var fonts: Array<Typeface?>
    private lateinit var mapOfFonts: Map<Int, String>
    private var appWidgetId: Int = AppWidgetManager.INVALID_APPWIDGET_ID
    private val repository = Repository.instance
    private var maxWidth = 0
    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences(SHARED_PREFERENCES, 0)
    }

    private val appWidgetManager: AppWidgetManager by lazy { AppWidgetManager.getInstance(this) }

    companion object {
        const val TAG = "MAIN_ACTIVITY"
    }

    private val customViewGroup: CustomViewGroup by lazy {
        CustomViewGroup(
            Size(
                repository.getDisplayWidth(sharedPreferences),
                repository.getDisplayHeight(sharedPreferences)
            ),
            repository.getBackgroundColor(sharedPreferences),
            repository.getBackgroundAlpha(sharedPreferences),
            repository.getLineSpacing(sharedPreferences),
            repository.getWidthPadding(sharedPreferences)
        )
    }

    private val hangulTime: CustomTextView by lazy {
        CustomTextView(
            fonts[typeface] ?: Typeface.DEFAULT,
            timeColor,
            timeAlpha,
            textSize,
            "",
            true,
            letterSpacing
        )
    }
    private val englishTime: CustomTextView by lazy {
        CustomTextView(
            fonts[typeface] ?: Typeface.DEFAULT,
            timeColor,
            timeAlpha,
            textSize,
            "",
            showEnglish,
            letterSpacing
        )
    }
    private val hangulDate: CustomTextView by lazy {
        CustomTextView(
            fonts[typeface] ?: Typeface.DEFAULT,
            dateColor,
            dateAlpha,
            textSize,
            "",
            showDate,
            letterSpacing
        )
    }
    private val englishDate: CustomTextView by lazy {
        CustomTextView(
            fonts[typeface] ?: Typeface.DEFAULT,
            dateColor,
            dateAlpha,
            textSize,
            "",
            showDate && showEnglish,
            letterSpacing
        )
    }
    private val scope = MainScope()
    private val date = Date()
    private val time = Time()
    private val paint = Paint()
    private val rect = Rect()

    private val timeHelper by lazy {
        TimeHelper().apply {
            getMapOfHours(applicationContext)
            getMapOfSinoKorean(applicationContext)
        }
    }
    private val dateHelper by lazy {
        DateHelper().apply {
            getMapOfMonths(applicationContext)
            getMapOfSinoKorean(applicationContext)
            getMapOfEnglishMonths(applicationContext)
        }
    }

    private val background: AppCompatImageView by lazy {
        findViewById<AppCompatImageView>(R.id.view_background)
    }

    private val fontDisplay: AppCompatTextView by lazy {
        findViewById<AppCompatTextView>(R.id.font_display)
    }

    private var colorPickerDialog = ColorPickerDialog().also {
        it.registerListener(this)
    }

    private var dateColor = Color.BLACK
    private var dateAlpha = 255
    private var timeColor = Color.BLACK
    private var timeAlpha = 255
    private var backgroundColor = Color.TRANSPARENT
    private var backgroundAlpha = 0
    private var typeface = 0
    private var textSize = 18.0f
    private var showDate = false
    private var showEnglish = false
    private var displayWidth = 400
    private var displayHeight = 200
    private var lineSpacing = 3
    private var letterSpacing = 0.0f
    private var sidePadding = 10


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appWidgetId = getAppWidgetId()
        initializeFonts(this)
        JodaTimeAndroid.init(this)
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, 0)
        getPreferences()
        maxWidth = getMaxWidth()
        paint.textSize = textSize
        paint.typeface = fonts[typeface]
        paint.letterSpacing = letterSpacing
        customViewGroup.listener = this
        customViewGroup.addView(hangulTime)
        customViewGroup.addView(englishTime)
        customViewGroup.addView(hangulDate)
        customViewGroup.addView(englishDate)
        hangulTime.listener = customViewGroup
        englishTime.listener = customViewGroup
        hangulDate.listener = customViewGroup
        englishDate.listener = customViewGroup
        setContentView(R.layout.activity_main)
        background.setImageBitmap(customViewGroup.bitmap)
        background.background = Grid()
        val layoutParams = background.layoutParams

        // populate font display text
        fontDisplay.text = mapOfFonts[typeface]


        // toggle date
        findViewById<SwitchCompat>(R.id.date_switch)
            .also {
                it.isChecked = showDate
                it.setOnCheckedChangeListener { _: CompoundButton, bool: Boolean ->
                    sharedPreferences.edit().putBoolean(DATE_BOOL, bool).apply()
                    hangulDate.mVisibility = bool
                    englishDate.mVisibility = bool && showEnglish
                    showDate = bool
                }
            }

        // toggle english
        findViewById<SwitchCompat>(R.id.english_switch)
            .also {
                it.isChecked = showEnglish
                it.setOnCheckedChangeListener { _: CompoundButton, bool: Boolean ->
                    sharedPreferences.edit().putBoolean(ENGLISH_BOOL, bool).apply()
                    englishDate.mVisibility = bool && showDate
                    englishTime.mVisibility = bool
                    showEnglish = bool
                }
            }

        layoutParams.width = displayWidth
        layoutParams.height = displayHeight
        background.layoutParams = layoutParams

        // change date color
        findViewById<AppCompatImageButton>(R.id.date_color).let {
            it.setOnClickListener {
                Toast.makeText(
                    applicationContext,
                    this.resources.getString(R.string.date),
                    Toast.LENGTH_SHORT
                ).show()
                colorPickerDialog.arguments?.putBundle(
                    TYPE,
                    Bundle().also { bundle -> bundle.putString(TYPE, DATE) })
                colorPickerDialog.type = DATE
                colorPickerDialog.show(supportFragmentManager, ColorPickerDialog.TAG)
            }
        }

        //change time color
        findViewById<AppCompatImageButton>(R.id.time_color).let {
            it.setOnClickListener {
                Toast.makeText(
                    applicationContext,
                    this.resources.getString(R.string.time),
                    Toast.LENGTH_SHORT
                ).show()
                colorPickerDialog.type = TIME
                colorPickerDialog.show(supportFragmentManager, ColorPickerDialog.TAG)
            }
        }

        // change background color
        findViewById<AppCompatImageButton>(R.id.background_color).let {
            it.setOnClickListener {
                Toast.makeText(
                    applicationContext,
                    this.resources.getString(R.string.background),
                    Toast.LENGTH_SHORT
                ).show()
                colorPickerDialog.type = BACKGROUND
                colorPickerDialog.show(supportFragmentManager, ColorPickerDialog.TAG)
            }
        }

        // letter spacing
        findViewById<SeekBar>(R.id.letter_spacing_slider).setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, p2: Boolean) {
                seekBar?.let {
                    val percentage = progress.percentage(it.max)
                    paint.letterSpacing = percentage
                    if (checkBounds()) {
                        letterSpacing = percentage
                        customViewGroup.listOfViews.forEach { view ->
                            view.mSpacing = percentage
                        }
                    }
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                Log.i(TAG, "letter spacing tracking is at  ${p0?.progress}")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                paint.letterSpacing = letterSpacing
                sharedPreferences.edit().putFloat(LETTER_SPACING, letterSpacing).apply()
            }
        }
        )

        //side padding
        findViewById<SeekBar>(R.id.side_slider).setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekbar: SeekBar?, progress: Int, p2: Boolean) {
                if (customViewGroup.contentWidth + progress.doubled() < maxWidth - 100 - customViewGroup.mSidePadding.doubled()) {
                    sidePadding = progress
                    customViewGroup.mSidePadding = progress
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                Log.i(TAG, "padding tracking is at  ${p0?.progress}")
            }

            override fun onStopTrackingTouch(seekbar: SeekBar?) {
                repository.setWidthPadding(sharedPreferences, sidePadding)
            }

        })

        //padding slider
        findViewById<SeekBar>(R.id.line_spacing_slider).let {
            it.progress = lineSpacing
            it.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                    customViewGroup.mPadding = progress
                    lineSpacing = progress
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                    Log.i(TAG, "padding tracking is at  ${p0?.progress}")
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    sharedPreferences.edit().putInt(LINE_SPACING, lineSpacing).apply()
                }

            })
        }

        // text size slider
        findViewById<SeekBar>(R.id.text_size_slider).let {
            it.progress = textSize.toInt()
            it.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, p2: Boolean) {
                    val mappedProgress = progress.mapToRange(0.00f, 50.0f, 10.0f, 50.0f)
                    paint.textSize = mappedProgress.toFloat()
                    if (checkBounds()) {
                        textSize = mappedProgress.toFloat()
                        customViewGroup.listOfViews.forEach { view ->
                            view.mTextSize = textSize
                        }
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {

                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    paint.textSize = textSize
                    sharedPreferences.edit().putInt(TEXT_SZ, textSize.toInt()).apply()
                }
            })
        }

// font style slider
        findViewById<SeekBar>(R.id.font_slider).let {
            it.progress = typeface
            it.max = fonts.lastIndex
            it.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                    paint.typeface = fonts[progress]
                    fontDisplay.text = mapOfFonts[progress] ?: ""
                    customViewGroup.listOfViews.forEach { view ->
                        view.mTypeface = fonts[progress] ?: Typeface.DEFAULT
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                    Log.i(TAG, "type face tracking started")
                }

                override fun onStopTrackingTouch(seekbar: SeekBar?) {
                    sharedPreferences.edit().putInt(FONT, seekbar?.progress ?: 0).apply()
                }

            })
        }

        findViewById<AppCompatButton>(R.id.ok_button).apply {
            setOnClickListener {
                updateRemoteViews()
                val resultValue = Intent().apply {
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                }
                setResult(Activity.RESULT_OK, resultValue)
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateDateAndTime()
        updateText()
        time()
    }

    override fun onPause() {
        super.onPause()
        scope.destroy()
    }

    override fun onColorSelect(type: String, values: IntArray) {
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, 0)
        Log.i(TAG, "type $type")
        when (type) {
            BACKGROUND -> {
                sharedPreferences.edit().putInt(BACKGROUND_SATURATION, values[SATURATION]).apply()
                sharedPreferences.edit().putInt(BACKGROUND_COLOR, values[COLOR]).apply()
                sharedPreferences.edit().putInt(BACKGROUND_ALPHA, values[TRANSPARENCY]).apply()
                backgroundColor = values[COLOR]
                backgroundAlpha = values[TRANSPARENCY]
                customViewGroup.mColor = backgroundColor
                customViewGroup.mAlpha = backgroundAlpha
                Log.i(TAG, "value = ${values[COLOR].alpha}")
            }

            TIME -> {
                sharedPreferences.edit().putInt(TIME_SATURATION, values[SATURATION]).apply()
                sharedPreferences.edit().putInt(TIME_COLOR, values[COLOR]).apply()
                sharedPreferences.edit().putInt(TIME_ALPHA, values[TRANSPARENCY]).apply()
                timeColor = values[COLOR]
                timeAlpha = values[TRANSPARENCY]
                hangulTime.mColor = timeColor
                hangulTime.mAlpha = timeAlpha
                englishTime.mColor = timeColor
                englishTime.mAlpha = timeAlpha
            }
            DATE -> {
                sharedPreferences.edit().putInt(DATE_SATURATION, values[SATURATION]).apply()
                sharedPreferences.edit().putInt(DATE_COLOR, values[COLOR]).apply()
                sharedPreferences.edit().putInt(DATE_ALPHA, values[TRANSPARENCY]).apply()
                dateColor = values[COLOR]
                dateAlpha = values[TRANSPARENCY]
                hangulDate.mColor = dateColor
                hangulDate.mAlpha = dateAlpha
                englishDate.mColor = dateColor
                englishDate.mAlpha = dateAlpha
            }
        }
        colorPickerDialog.dismiss()
    }

    fun time() {
        scope.launch {
            delay(60000)
            time()
            updateDateAndTime()
            updateText()
        }
    }

    private fun updateDateAndTime() {
        date.updateHangul(dateHelper.getHangulDate())
        date.updateEnglish(dateHelper.getEnglishDate())
        time.updateHangul(timeHelper.getHangulTime())
        time.updateEnglish(timeHelper.getEnglishTime())
    }

    class MainScope : CoroutineScope, LifecycleObserver {
        private val job = SupervisorJob()
        override val coroutineContext: CoroutineContext
            get() = job + Dispatchers.Main

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun destroy() = coroutineContext.cancelChildren()
    }

    private fun updateText() {
        hangulTime.mText = time.getHangulTime()
        englishTime.mText = time.getEnglishTime()
        hangulDate.mText = date.getHangulDate()
        englishDate.mText = date.getEnglishDate()
    }

    private fun initializeFonts(context: Context) {
        fonts = arrayOf(
            Typeface.DEFAULT,
            ResourcesCompat.getFont(context, R.font.bmhanna_air),
            ResourcesCompat.getFont(context, R.font.bmhanna_pro),
            ResourcesCompat.getFont(context, R.font.bmhanna_11yrs),
            ResourcesCompat.getFont(context, R.font.bmkiranghaerang),
            ResourcesCompat.getFont(context, R.font.bmdohyeon),
            ResourcesCompat.getFont(context, R.font.typo_dodam_b),
            ResourcesCompat.getFont(context, R.font.typo_dodam_m),
            ResourcesCompat.getFont(context, R.font.typo_eoulrim_b),
            ResourcesCompat.getFont(context, R.font.typo_eoulrim_l),
            ResourcesCompat.getFont(context, R.font.sunflower_bold),
            ResourcesCompat.getFont(context, R.font.sunflower_light),
            ResourcesCompat.getFont(context, R.font.sunflower_medium),
            ResourcesCompat.getFont(context, R.font.blackhanssans_regular)
        )
        mapOfFonts = mapOf(
            0 to context.resources.getString(R.string.default_font),
            1 to context.resources.getString(R.string.bmhanna_air),
            2 to context.resources.getString(R.string.bmhanna_pro),
            3 to context.resources.getString(R.string.bmhanna_11yrs),
            4 to context.resources.getString(R.string.bmkiranghaerang),
            5 to context.resources.getString(R.string.bmdhyeon),
            6 to context.resources.getString(R.string.typo_dodam_b),
            7 to context.resources.getString(R.string.typo_dodam_m),
            8 to context.resources.getString(R.string.typo_eoulrim_b),
            9 to context.resources.getString(R.string.typo_eoulrim_l),
            10 to context.resources.getString(R.string.sunflower_bold),
            11 to context.resources.getString(R.string.sunflower_light),
            12 to context.resources.getString(R.string.sunflower_medium),
            13 to context.resources.getString(R.string.black_hans_sans_regular)
        )
    }

    private fun getAppWidgetId(): Int {
        return intent.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID
    }


    private fun updateRemoteViews() {
        background.background = null
        val remoteViews = RemoteViews(this.packageName, R.layout.widget_image_only)
        remoteViews.setImageViewBitmap(R.id.widget_image, customViewGroup.bitmap)
        val componentName = ComponentName(this, HangulDateTimeWidgetProvider::class.java)
        for (id in appWidgetManager.getAppWidgetIds(componentName)) {
            appWidgetManager.updateAppWidget(id, remoteViews)
        }
    }

    private fun getPreferences() {
        timeColor = repository.getTimeColor(sharedPreferences)
        timeAlpha = repository.getTimeAlpha(sharedPreferences)
        dateColor = repository.getDateColor(sharedPreferences)
        dateAlpha = repository.getDateAlpha(sharedPreferences)
        backgroundColor = repository.getBackgroundColor(sharedPreferences)
        backgroundAlpha = repository.getBackgroundAlpha(sharedPreferences)
        textSize = repository.getTextSize(sharedPreferences).toFloat()
        typeface = repository.getTypeFace(sharedPreferences)
        showEnglish = repository.getEnglishVisibility(sharedPreferences)
        showDate = repository.getDateVisibility(sharedPreferences)
        displayWidth = repository.getDisplayWidth(sharedPreferences)
        displayHeight = repository.getDisplayHeight(sharedPreferences)
        lineSpacing = repository.getLineSpacing(sharedPreferences)
        letterSpacing = repository.getLetterSpacing(sharedPreferences)
        sidePadding = repository.getWidthPadding(sharedPreferences)
    }

    override fun onBitmapChanged(bitmap: Bitmap) {
        Log.i(TAG, "called")
        val layoutParams = background.layoutParams
        layoutParams.height = bitmap.height
        layoutParams.width = bitmap.width
        background.layoutParams = layoutParams
        background.setImageBitmap(bitmap)
        repository.setDisplayWidth(sharedPreferences, bitmap.width)
        repository.setDisplayHeight(sharedPreferences, bitmap.height)
    }

    private fun getMaxWidth(): Int {
        val displayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            this.display?.getRealMetrics(displayMetrics)
        } else {
            this.getSystemService(Context.WINDOW_SERVICE).let {
                it as WindowManager
                it.defaultDisplay.getMetrics(displayMetrics)
            }
        }
        return displayMetrics.widthPixels.coerceAtMost(displayMetrics.heightPixels)
    }

    fun checkBounds(): Boolean {
        val text = customViewGroup.widestString
        paint.getTextBounds(text, 0, text.length, rect)
        return rect.width() < maxWidth - 100 - customViewGroup.mSidePadding.doubled()
    }

    override fun onStop() {
        super.onStop()
        Log.i("TAG","ON STOP CALLED")
        updateRemoteViews()
        val resultValue = Intent().apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        setResult(Activity.RESULT_OK, resultValue)
    }
}

