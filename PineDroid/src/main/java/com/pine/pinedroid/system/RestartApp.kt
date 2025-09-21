package com.pine.pinedroid.system

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.pine.pinedroid.utils.appContext

fun restartApp(delay: Long = 200) {
    val intent = appContext.packageManager.getLaunchIntentForPackage(appContext.packageName)
    intent?.addFlags(
        Intent.FLAG_ACTIVITY_CLEAR_TOP or
            Intent.FLAG_ACTIVITY_CLEAR_TASK or
            Intent.FLAG_ACTIVITY_NEW_TASK)

    val pendingIntent = PendingIntent.getActivity(
        appContext, 0, intent,
        PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val am = appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    am.set(AlarmManager.RTC, System.currentTimeMillis() + delay, pendingIntent)

    // 杀掉进程
    Runtime.getRuntime().exit(0)
}