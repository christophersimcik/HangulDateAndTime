package com.csimck.hangulwidget

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

const val ACTION_CLOCK_TIC = "com.csimck.hangulwidget.ACTION_CLOCK_TIC"

class TestReceiver : BroadcastReceiver() {
    companion object {
        const val TAG = "TEST_RECEIVER"
    }

    private val alarmHelper = AlarmHelper()

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(TAG, "Received : ")
        val intentAction = intent?.action
        if(intentAction == Intent.ACTION_BOOT_COMPLETED || intentAction == ACTION_CLOCK_TIC){
        alarmHelper.setAlarm(context)
        val int = Intent(context, HangulDateTimeWidgetProvider::class.java)
        int.apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        }
        context?.sendBroadcast(int)
        }
    }
}