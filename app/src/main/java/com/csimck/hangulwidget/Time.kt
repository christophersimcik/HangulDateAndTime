package com.csimck.hangulwidget

class Time(
    private var hangulTimeFrame: String = "",
    private var hangulHour: String = "",
    private var hangulMinute: String = "",
    private var englishTimeFrame: String = "",
    private var englishHour: String = "",
    private var englishMinute: String = ""
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