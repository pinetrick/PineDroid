package com.pine.pinedroid.utils

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.pine.pinedroid.R
import com.pine.pinedroid.isDebug
import com.pine.pinedroid.utils.ui.dp2Px


fun debugToast(message: String) {
    if (isDebug) {
        toast(message)
    }
}

fun toast(message: String, title: String? = null, icon: Int? = null, duration: Long = 1000L) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        // 当前在主线程，直接显示
        showAppToast(message, title, icon, duration)

    } else {
        // 当前在子线程，使用 Handler 切换到主线程
        Handler(Looper.getMainLooper()).post {
            showAppToast(message, title, icon, duration)
        }
    }
}


private fun showAppToast(
    message: String,
    title: String? = null,
    iconRes: Int? = null,
    duration: Long = 3000L
) {
    val inflater = LayoutInflater.from(appContext)
    val view = inflater.inflate(R.layout.toast_general, null)

    val iconView = view.findViewById<ImageView>(R.id.ivToastIcon)
    val titleView = view.findViewById<TextView>(R.id.tvToastTitle)
    val messageView = view.findViewById<TextView>(R.id.tvToastMessage)

    messageView.text = message

    if (!title.isNullOrBlank()) {
        titleView.text = title
        titleView.visibility = View.VISIBLE
    } else {
        titleView.visibility = View.GONE
    }

    if (iconRes != null) {
        iconView.setImageResource(iconRes)
        iconView.visibility = View.VISIBLE
    } else {
        iconView.visibility = View.GONE
    }

    // 使用自定义WindowManager显示，可以控制显示时长
    showCustomToast(view, duration)
}

private fun showCustomToast(toastView: View, duration: Long) {
    val context = appContext
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    // 设置LayoutParams
    val params = WindowManager.LayoutParams().apply {
        type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
        flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        format = PixelFormat.TRANSLUCENT
        gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        width = WindowManager.LayoutParams.WRAP_CONTENT
        height = WindowManager.LayoutParams.WRAP_CONTENT
        y = 200
    }

    // 添加到窗口
    windowManager.addView(toastView, params)

    // 使用Handler延迟移除
    Handler(Looper.getMainLooper()).postDelayed({
        try {
            windowManager.removeView(toastView)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }, duration)
}