package com.pine.pinedroid.ui.float_window

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import com.pine.pinedroid.utils.activityContext
import com.pine.pinedroid.utils.appContext
import com.pine.pinedroid.utils.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

object FloatingWindowHelper {

    suspend fun showFloatingWindow() {
        if (!waitPermission()) return


        val windowManager = activityContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val textView = TextView(activityContext).apply {
            text = "悬浮窗Lib"
            textSize = 16f
            setBackgroundColor(0x55FF0000)
            setTextColor(0xFFFFFFFF.toInt())
            setPadding(20, 20, 20, 20)
        }

        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 300
        }

        windowManager.addView(textView, layoutParams)

    }

    private suspend fun waitPermission(): Boolean {
        // Android 6+ 检查权限
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(appContext))
            return true

        // 需要在协程中调用 suspend
        delay(1000)
        RedirectToSettingEnableFloatWindow()

        val granted = withTimeoutOrNull(30_000) {
            while (!Settings.canDrawOverlays(appContext)) {
                delay(500)
            }
            true
        } ?: false

        if(!granted) {
            toast("Require alert windows permission")
        }
        return granted

    }



}
