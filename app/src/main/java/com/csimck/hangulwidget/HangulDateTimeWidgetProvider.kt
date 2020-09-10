package com.csimck.hangulwidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.*
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.util.Size
import android.view.WindowManager
import android.widget.RemoteViews
import androidx.core.content.res.ResourcesCompat
import net.danlew.android.joda.JodaTimeAndroid

class HangulDateTimeWidgetProvider : AppWidgetProvider() {

    private val repository = Repository.instance
    private lateinit var customViewGroup: CustomViewGroup
    private var alarmHelper = AlarmHelper()

    private val time = Time()
    private val date = Date()
    private var timeColor = Color.BLACK
    private var timeAlpha = MAX_ALPHA
    private var dateColor = Color.BLACK
    private var dateAlpha = MAX_ALPHA
    private var backgroundColor = Color.BLACK
    private var backgroundAlpha = MAX_ALPHA
    private var textSize = DEFAULT_TEXT_SZ
    private var lineSpacing = 0
    private var sidePadding = 0
    private var letterSpacing = 0.0f
    private var typeface = 0
    private var displayEnglish = false
    private var displayDate = false
    private var displayWidth = 400
    private var displayHeight = 200

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        Log.i(TAG, "ON UPDATE")
        appWidgetIds?.forEach {
            val views = RemoteViews(context?.packageName, R.layout.widget_image_only)
            if (this::customViewGroup.isInitialized) {
                views.setImageViewBitmap(R.id.widget_image, customViewGroup.bitmap)
            }
            appWidgetManager?.updateAppWidget(it, views)
            Log.i(TAG, "update called")
        }
    }

    override fun onRestored(context: Context?, oldWidgetIds: IntArray?, newWidgetIds: IntArray?) {
        super.onRestored(context, oldWidgetIds, newWidgetIds)
        Log.i(TAG, "ON RESTORED")
    }

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        alarmHelper.setAlarm(context)
        Log.i(TAG, "ON ENABLED")
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
        alarmHelper.cancelAlarm(context)
        Log.i(TAG, "ON DISABLED")
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        Log.i(TAG, "ON RECEIVED")
        JodaTimeAndroid.init(context)

        context?.let {
            val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES, 0)
            getPrefs(sharedPreferences)
            val fonts = getFonts(context)
            customViewGroup =
                CustomViewGroup(
                    Size(displayWidth, displayHeight),
                    backgroundColor,
                    backgroundAlpha,
                    lineSpacing,
                    sidePadding
                )
            val timeHelper = TimeHelper().apply {
                getMapOfHours(context)
                getMapOfSinoKorean(context)
            }
            val dateHelper = DateHelper().apply {
                getMapOfMonths(context)
                getMapOfSinoKorean(context)
                getMapOfEnglishMonths(context)
            }
            updateTimeAndDate(timeHelper, dateHelper)
            val hangulTime = CustomTextView(
                fonts[typeface] ?: Typeface.DEFAULT,
                timeColor,
                timeAlpha,
                textSize.toFloat(),
                time.getHangulTime(),
                true,
                letterSpacing
            )
            customViewGroup.addView(hangulTime)
            val englishTime = CustomTextView(
                fonts[typeface] ?: Typeface.DEFAULT,
                timeColor,
                timeAlpha,
                textSize.toFloat(),
                time.getEnglishTime(),
                displayEnglish,
                letterSpacing
            )
            customViewGroup.addView(englishTime)

            val hangulDate = CustomTextView(
                fonts[typeface] ?: Typeface.DEFAULT,
                dateColor,
                dateAlpha,
                textSize.toFloat(),
                date.getHangulDate(),
                displayDate,
                letterSpacing
            )
            customViewGroup.addView(hangulDate)

            val englishDate = CustomTextView(
                fonts[typeface] ?: Typeface.DEFAULT,
                dateColor,
                dateAlpha,
                textSize.toFloat(),
                date.getEnglishDate(),
                displayEnglish && displayDate,
                letterSpacing
            )
            customViewGroup.addView(englishDate)
        }
        customViewGroup.drawToBitmap()
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context?.packageName ?: "", javaClass.name)
        val ids = appWidgetManager.getAppWidgetIds(componentName)
        onUpdate(context, appWidgetManager, ids)
    }

    private fun updateTimeAndDate(timeHelper: TimeHelper, dateHelper: DateHelper) {
        time.updateHangul(timeHelper.getHangulTime())
        time.updateEnglish(timeHelper.getEnglishTime())
        date.updateHangul(dateHelper.getHangulDate())
        date.updateEnglish(dateHelper.getEnglishDate())
    }

    private fun getPrefs(sharedPreferences: SharedPreferences) {
        timeColor = repository.getTimeColor(sharedPreferences)
        timeAlpha = repository.getTimeAlpha(sharedPreferences)
        dateColor = repository.getDateColor(sharedPreferences)
        dateAlpha = repository.getDateAlpha(sharedPreferences)
        backgroundColor = repository.getBackgroundColor(sharedPreferences)
        backgroundAlpha = repository.getBackgroundAlpha(sharedPreferences)
        textSize = repository.getTextSize(sharedPreferences)
        typeface = repository.getTypeFace(sharedPreferences)
        displayEnglish = repository.getEnglishVisibility(sharedPreferences)
        displayDate = repository.getDateVisibility(sharedPreferences)
        displayWidth = repository.getDisplayWidth(sharedPreferences)
        displayHeight = repository.getDisplayHeight(sharedPreferences)
        lineSpacing = repository.getLineSpacing(sharedPreferences)
        letterSpacing = repository.getLetterSpacing(sharedPreferences)
        sidePadding = repository.getWidthPadding(sharedPreferences)
    }
}

private fun getFonts(context: Context): Array<Typeface?> {
    return arrayOf(
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
}






