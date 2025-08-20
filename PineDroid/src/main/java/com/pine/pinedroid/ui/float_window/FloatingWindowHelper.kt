package com.pine.pinedroid.ui.float_window

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
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

    suspend fun showFloatingWindow(view: View, dragable: Boolean = false) {
        if (!waitPermission()) return

        val windowManager = appContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager


        var layoutType = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            layoutType = WindowManager.LayoutParams.TYPE_PHONE
        }

        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 1000
            y = 1000
        }

        if (dragable) {
            // 在 showFloatingWindow 里加

            view.setOnTouchListener(object : View.OnTouchListener {
                private var lastX = 0
                private var lastY = 0

                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            lastX = event.rawX.toInt()
                            lastY = event.rawY.toInt()
                        }
                        MotionEvent.ACTION_MOVE -> {
                            val dx = event.rawX.toInt() - lastX
                            val dy = event.rawY.toInt() - lastY

                            layoutParams.x += dx
                            layoutParams.y += dy
                            windowManager.updateViewLayout(view, layoutParams)

                            lastX = event.rawX.toInt()
                            lastY = event.rawY.toInt()
                        }
                    }
                    return true
                }
            })
        }


        windowManager.addView(view, layoutParams)
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
