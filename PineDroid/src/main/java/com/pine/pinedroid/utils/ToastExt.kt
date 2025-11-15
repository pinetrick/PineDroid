package com.pine.pinedroid.utils

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.pine.pinedroid.PineConfig

fun debugToast(message: String) {
    if (PineConfig.getIsDebug()) {
        toast(message)
    }
}

fun toast(message: String) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        // 当前在主线程，直接显示
        Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show()
    } else {
        // 当前在子线程，使用 Handler 切换到主线程
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show()
        }
    }
}

fun toastLong(message: String) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        // 当前在主线程，直接显示
        Toast.makeText(appContext, message, Toast.LENGTH_LONG).show()
    } else {
        // 当前在子线程，使用 Handler 切换到主线程
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(appContext, message, Toast.LENGTH_LONG).show()
        }
    }
}