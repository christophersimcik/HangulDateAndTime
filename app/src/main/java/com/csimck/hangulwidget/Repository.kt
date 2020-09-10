package com.csimck.hangulwidget

import android.content.SharedPreferences
import android.graphics.Color
import android.util.Log
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.util.*

const val DEFAULT_TEXT_SZ = 18

class Repository private constructor() {
    private object Holder {
        val INSTANCE = Repository()
        const val TAG = "REPOSITORY"
    }

    companion object {
        val instance: Repository by lazy { Holder.INSTANCE }
    }

    fun getDateTime(): DateTime {
        Log.i(TAG, "zone = ${DateTime.now().zone}")
        val dateTimeZone = DateTimeZone.forTimeZone(TimeZone.getDefault())
        return DateTime.now().withZone(dateTimeZone)
    }

    fun getTimeColor(sharedPreferences: SharedPreferences): Int {
        return sharedPreferences.getInt(TIME_COLOR, Color.BLACK)
    }

    fun getTimeAlpha(sharedPreferences: SharedPreferences): Int {
        return sharedPreferences.getInt(TIME_ALPHA, MAX_ALPHA)
    }

    fun getDateColor(sharedPreferences: SharedPreferences): Int {
        return sharedPreferences.getInt(DATE_COLOR, Color.BLACK)
    }

    fun getDateAlpha(sharedPreferences: SharedPreferences): Int {
        return sharedPreferences.getInt(DATE_ALPHA, MAX_ALPHA)
    }

    fun getTextSize(sharedPreferences: SharedPreferences): Int {
        return sharedPreferences.getInt(TEXT_SZ, DEFAULT_TEXT_SZ)
    }

    fun getBackgroundColor(sharedPreferences: SharedPreferences): Int {
        return sharedPreferences.getInt(BACKGROUND_COLOR, Color.TRANSPARENT)
    }

    fun getBackgroundAlpha(sharedPreferences: SharedPreferences): Int {
        return sharedPreferences.getInt(BACKGROUND_ALPHA, 0)
    }

    fun getDateVisibility(sharedPreferences: SharedPreferences): Boolean {
        return sharedPreferences.getBoolean(DATE_BOOL, false)
    }

    fun getEnglishVisibility(sharedPreferences: SharedPreferences): Boolean {
        return sharedPreferences.getBoolean(ENGLISH_BOOL, false)
    }

    fun getTypeFace(sharedPreferences: SharedPreferences): Int {
        return sharedPreferences.getInt(FONT, 0)
    }

    fun getLineSpacing(sharedPreferences: SharedPreferences): Int {
        return sharedPreferences.getInt(LINE_SPACING, 0)
    }

    fun getLetterSpacing(sharedPreferences: SharedPreferences): Float {
        return sharedPreferences.getFloat(LETTER_SPACING, 0.0f)
    }

    fun setDisplayWidth(sharedPreferences: SharedPreferences, width: Int) {
        sharedPreferences.edit().putInt(DISPLAY_WIDTH, width).apply()
    }

    fun getDisplayWidth(sharedPreferences: SharedPreferences): Int {
        return sharedPreferences.getInt(DISPLAY_WIDTH, 400)
    }

    fun setDisplayHeight(sharedPreferences: SharedPreferences, height: Int) {
        sharedPreferences.edit().putInt(DISPLAY_HEIGHT, height).apply()
    }

    fun getDisplayHeight(sharedPreferences: SharedPreferences): Int {
        return sharedPreferences.getInt(DISPLAY_HEIGHT, 200)
    }

    fun setWidthPadding(sharedPreferences: SharedPreferences, padding : Int){
        sharedPreferences.edit().putInt(SIDE_PADDING,padding).apply()
    }

    fun getWidthPadding(sharedPreferences: SharedPreferences) : Int{
        return sharedPreferences.getInt(SIDE_PADDING,3)
    }
}