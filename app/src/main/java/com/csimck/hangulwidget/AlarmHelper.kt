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
        const val MILLIS_IN_MINUTE: Long = 60000
    }

    lateinit var alarmManager: AlarmManager
    lateinit var appWidgetManager: AppWidgetManager

    fun cancelAlarm(context: Context?) {
        alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(createIntent(context))
    }

    fun setAlarm(context: Context?) {
        alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        appWidgetManager = AppWidgetManager.getInstance(context)
        val nextAlarm: Long = DateTime.now().let {
            val seconds = it.millis % MILLIS_IN_MINUTE
            val timeMinusSeconds = it.millis - seconds
            timeMinusSeconds + MILLIS_IN_MINUTE
        }
        Log.i(TAG, " nxt alrm = $nextAlarm")
        alarmManager.setExact(
            AlarmManager.RTC,
            nextAlarm,
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