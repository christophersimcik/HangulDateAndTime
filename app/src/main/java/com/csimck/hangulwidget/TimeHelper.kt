package com.csimck.hangulwidget

import android.content.Context
import android.util.Log
import android.widget.NumberPicker
import org.joda.time.format.DateTimeFormat
import java.text.DecimalFormat
import java.text.Format

const val TAG = "TimeHelper"
const val TIMEFRAME = 0
const val HR = 1
const val MIN = 2
const val AM = "오전"
const val PM = "오후"

// 1 to 6 am
const val DAWN = "새벽"

// 7 to 11 am
const val MORNING = "아침"

// 5 to 8 pm
const val EVENING = ""

// 8 to 12 pm
const val NIGHT = ""

class TimeHelper(var hours: Map<String, String>? = null, var mins: Map<String, String>? = null) {
    private val formatTime = DateTimeFormat.forPattern("a,h,m")
    private val repository = Repository.instance
    fun getHangulTime(): String {
        val time = getTimeAsArray()
        val hour = time[HR]
        val minute = time[MIN]
        val meridian = time[TIMEFRAME]
        val stringBuilder = StringBuilder()
        stringBuilder.append(getHangulTimeFrame(meridian) + "-")
        stringBuilder.append(getHangulHour(hour) + "-")
        stringBuilder.append(getHangulMinute(minute))
        return stringBuilder.toString()
    }

     fun getEnglishTime(): String {
        val time = getTimeAsArray()
        val hour = time[HR]
        val minute = "%02d".format(time[MIN].toInt())
        val meridian = time[TIMEFRAME]
        val stringBuilder = StringBuilder()
        stringBuilder.append("$meridian-")
        stringBuilder.append("$hour-")
        stringBuilder.append("$minute")
        return stringBuilder.toString()
    }

     fun getHangulTimeFrame(meridian: String): String {
        var hangulMeridian = ""
        when (meridian) {
            "AM" -> hangulMeridian = AM
            "PM" -> hangulMeridian = PM
        }
         return hangulMeridian
     }

    fun getHangulHour(hour: String): String {
        return hours?.get(hour) ?: ""
    }

    fun getHangulMinute(min: String): String {
        return mins?.get(min) ?: ""
    }

    private fun getTimeAsArray(): List<String> {
        Log.i(TAG, repository.getDateTime().toString(formatTime))
        return repository.getDateTime().toString(formatTime).split(",")
    }

    fun getMapOfHours(context: Context){
        hours = context.resources.getStringArray(R.array.hours)
            .associate { string ->
                val list = string.split("|")
                list[0] to list[1]
            }
    }
    fun getMapOfSinoKorean(context: Context) {
        mins = context.resources.getStringArray(R.array.sino_korean)
            .associate {
                val list = it.split("|")
                list[0] to list[1]
            }
    }
}