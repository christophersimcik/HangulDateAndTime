package com.csimck.hangulwidget

class Date(
    private var hangulYear: String = "",
    private var hangulMonth: String = "",
    private var hangulDay: String = "",
    private var englishYear: String = "",
    private var englishMonth: String = "",
    private var englishDay: String = ""
) {

    fun updateEnglish(time: String) {
        val time = time.split("-")
        englishYear = if(time[YEAR] != ""){time[YEAR] + " "}else{""}
        englishMonth = time[MONTH]
        englishDay = if(time[DAY] != ""){" " + time[DAY]}else{""}
    }
    fun updateHangul(time: String) {
        val time = time.split("-")
        hangulYear = if(time[YEAR] != ""){time[YEAR] + " "}else{""}
        hangulMonth = time[MONTH]
        hangulDay = if(time[DAY] != ""){" " + time[DAY]}else{""}
    }

    fun getHangulDate(): String {
        return hangulYear + hangulMonth + hangulDay
    }

    fun getEnglishDate(): String {
        return englishYear + englishMonth + englishDay
    }
}