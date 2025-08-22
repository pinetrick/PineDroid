package com.pine.pinedroid.utils

import android.content.pm.ApplicationInfo

object PineApp {
    fun isAppDebug(): Boolean {
        return try {
            (appContext.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        } catch (e: Exception) {
            false
        }
    }
}