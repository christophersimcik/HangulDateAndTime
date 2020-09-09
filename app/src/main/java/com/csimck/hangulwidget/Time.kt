package com.csimck.hangulwidget

class Time(
    var hangulTimeFrame: String = "",
    var hangulHour: String = "",
    var hangulMinute: String = "",
    var englishTimeFrame: String = "",
    var englishHour: String = "",
    var englishMinute: String = ""
) {
    fun updateHangul(time: String) {
        val time = time.split("-")
        hangulTimeFrame = if (time[TIMEFRAME] != "") {
            time[TIMEFRAME] + " "
        } else {
            ""
        }
        hangulHour = time[HR]
        hangulMinute = if (time[MIN] != "") {
            " " + time[MIN]
        } else {
            ""
        }
    }

    fun updateEnglish(time: String) {
        val time = time.split("-")
        englishTimeFrame = if (time[TIMEFRAME] != "") {
            time[TIMEFRAME] + " "
        } else {
            ""
        }
        englishHour = time[HR]
        englishMinute = if (time[MIN] != "") {
            ":" + time[MIN]
        } else {
            ""
        }
    }

    fun getHangulTime(): String {
        return hangulTimeFrame + hangulHour + hangulMinute
    }

    fun getEnglishTime(): String {
        return englishTimeFrame + englishHour + englishMinute
    }
}