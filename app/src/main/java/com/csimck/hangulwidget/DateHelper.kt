package com.csimck.hangulwidget
import android.content.Context
import org.joda.time.format.DateTimeFormat

const val YEAR = 0
const val MONTH = 1
const val DAY = 2

class DateHelper(
    private var months: Map<String,String>? = null,
    private var nums: Map<String,String>? = null,
    private var englishMonths : Map<String,String>? = null
) {
    private val formatDate = DateTimeFormat.forPattern("yyyy,M,d")
    private val repository = Repository.instance

    fun getEnglishDate(): String {
        val time = getDateAsArray()
        val year = time[YEAR]
        val month = englishMonths?.get(time[MONTH])
        val day = time[DAY]
        return "$year-$month-$day"
    }

    fun getHangulDate(): String {
        val time = getDateAsArray()
        val year = time[YEAR]
        val month = time[MONTH]
        val day = time[DAY]
        val stringBuilder = StringBuilder()
        stringBuilder.append(getHangulYear(year) + "-")
        stringBuilder.append(getHangulMonth(month) + "-")
        stringBuilder.append(getHangulDay(day))
        return stringBuilder.toString()
    }

    private fun getHangulYear(year: String): String {
        val yrInt = year.toInt()
        val thousands: Int = yrInt / 1000
        val hundreds: Int = yrInt % 1000 / 100
        val tens: Int = yrInt % 1000 % 100 / 10
        val ones = yrInt % 1000 % 100 % 10
        val stringBuilder = StringBuilder()
        if (thousands != 0) {
            stringBuilder.append(nums?.get(thousands.toString()))
            stringBuilder.append(nums?.get("1000"))
        }
        if (hundreds != 0) {
            stringBuilder.append(nums?.get(hundreds.toString()))
            stringBuilder.append(nums?.get("100"))
        }
        if (tens != 0) {
            stringBuilder.append(nums?.get(tens.toString()))
            stringBuilder.append(nums?.get("10"))
        }
        if (ones != 0) {
            stringBuilder.append(nums?.get(ones.toString()))
        }
        return stringBuilder.toString()
    }

    private fun getHangulMonth(month: String): String {
        return months?.get(month) ?: ""
    }

    private fun getHangulDay(day: String): String {
        return nums?.get(day) ?: ""
    }

    private fun getDateAsArray(): List<String> {
        return repository.getDateTime().toString(formatDate).split(",")
    }

    fun getMapOfMonths(context: Context){
        months = context.resources.getStringArray(R.array.months)
            .associate { string ->
                val list = string.split("|")
                list[0] to list[1]
            }
    }

    fun getMapOfEnglishMonths(context : Context){
        englishMonths = context.resources.getStringArray(R.array.english_months)
            .associate { string ->
                val list = string.split("|")
                list[0] to list[1]
            }
    }

    fun getMapOfSinoKorean(context: Context){
        nums = context.resources.getStringArray(R.array.sino_korean)
            .associate {
                val list = it.split("|")
                list[0] to list[1]
            }
    }
}