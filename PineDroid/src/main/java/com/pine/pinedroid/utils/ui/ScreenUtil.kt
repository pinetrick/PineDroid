package com.pine.pinedroid.utils.ui

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.pine.pinedroid.utils.appContext

object ScreenUtil {


    private var screenWidth: Int = 0
    private var screenHeight: Int = 0
    private var density: Float = 0f

    private fun initMetrics() {
        if (screenWidth == 0 || screenHeight == 0 || density == 0f) {
            val metrics: DisplayMetrics = appContext.resources.displayMetrics
            screenWidth = metrics.widthPixels
            screenHeight = metrics.heightPixels
            density = metrics.density
        }
    }

    fun getStatusBarHeightPx(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = (appContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager).currentWindowMetrics
            val insets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.statusBars())
            insets.top
        } else {
            // 兼容老版本
            val resourceId = appContext.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) appContext.resources.getDimensionPixelSize(resourceId) else 0
        }
    }

    fun getStatusBarHeightDp(): Float {
        return pxToDp(getStatusBarHeightPx().toFloat())
    }

    fun getScreenWidthPx(): Int {
        initMetrics()
        return screenWidth
    }

    fun getScreenHeightPx(): Int {
        initMetrics()
        return screenHeight
    }

    fun getScreenWidthDp(): Float {
        initMetrics()
        return screenWidth / density
    }

    fun getScreenHeightDp(): Float {
        initMetrics()
        return screenHeight / density
    }

    fun dpToPx(dp: Float): Int {
        initMetrics()
        return (dp * density).toInt()
    }

    fun pxToDp(px: Float): Float {
        initMetrics()
        return px / density
    }
}