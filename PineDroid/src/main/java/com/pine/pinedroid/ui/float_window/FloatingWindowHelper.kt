package com.pine.pinedroid.ui.float_window

import android.content.Context
import android.graphics.PixelFormat
import android.provider.Settings
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import com.pine.pinedroid.utils.appContext
import com.pine.pinedroid.utils.sp
import com.pine.pinedroid.utils.toast
import com.pine.pinedroid.utils.ui.ScreenUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull

object FloatingWindowHelper {

    suspend fun showFloatingWindow(view: View, draggable: Boolean = false) {
        if (!waitPermission()) return

        val windowManager = appContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        windowManager.addView(view, layoutParams)

        if (draggable) {
            // 在 view 内部偏移
            layoutParams.x = (sp("FloatWindowLastX") ?: 100).coerceIn(
                0,
                ScreenUtil.getScreenWidthPx() - view.width
            )
            layoutParams.y = (sp("FloatWindowLastY") ?: 100).coerceIn(
                0,
                ScreenUtil.getScreenHeightPx() - view.height
            )
            windowManager.updateViewLayout(view, layoutParams)

            var lastX = 0f
            var lastY = 0f

            view.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        lastX = event.rawX
                        lastY = event.rawY
                        true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        val dx = event.rawX - lastX
                        val dy = event.rawY - lastY

                        layoutParams.x += dx.toInt()
                        layoutParams.y += dy.toInt()
                        windowManager.updateViewLayout(view, layoutParams)

                        lastX = event.rawX
                        lastY = event.rawY

                        sp("FloatWindowLastX", layoutParams.x)
                        sp("FloatWindowLastY", layoutParams.y)
                        true
                    }

                    MotionEvent.ACTION_UP -> {
                        // 判断是否是点击（小移动距离）
                        val dx = event.rawX - lastX
                        val dy = event.rawY - lastY
                        if (dx * dx + dy * dy < 25) { // 5px以内认为是点击
                            v.performClick()
                        }
                        true
                    }

                    else -> false
                }
            }

        }


    }


    private suspend fun waitPermission(): Boolean {
        // Android 6+ 检查权限
        if (Settings.canDrawOverlays(appContext))
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

        if (!granted) {
            toast("Require alert windows permission")
        }
        return granted

    }


}
