package com.csimck.hangulwidget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import org.joda.time.DateTime

class AlarmHelper {
    companion object {
        const val REQUEST_CODE = 0
        const val TAG = "ALARM_HELPER"
    }

    lateinit var alarmManager: AlarmManager
    lateinit var appWidgetManager: AppWidgetManager

    fun setAlarm(context: Context?) {
        alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        appWidgetManager = AppWidgetManager.getInstance(context)
        val nextAlarmTime: Long = DateTime.now().let {
            val timeMinusSeconds = it.millis - it.millis % 60000
            Log.i(TAG, "time = ${it.millis}")
            Log.i(TAG, "time minus secs = $timeMinusSeconds")
            Log.i(TAG, "next min = ${timeMinusSeconds + 60000}")
            it.millis + 60000
        }
        alarmManager.setExact(
            AlarmManager.RTC,
            nextAlarmTime,
            createIntent(context)
        )
    }

    private fun createIntent(context: Context?): PendingIntent {
        val intent = Intent(context, TestReceiver::class.java)
        intent.action = ACTION_CLOCK_TIC
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}