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
import androidx.annotation.DrawableRes
import com.pine.pinedroid.R
import com.pine.pinedroid.isDebug
import java.util.concurrent.LinkedBlockingQueue

fun debugToast(message: String) {
    if (isDebug) {
        toast(message)
    }
}

fun toast(message: String, title: String? = null, @DrawableRes icon: Int? = null, duration: Long = 1000L, onClick: (() -> Unit)? = null) {
    ToastExt.toast(message, title, icon, duration, onClick)
}

object ToastExt {
    // 队列存储等待显示的Toast
    private val toastQueue = LinkedBlockingQueue<ToastItem>()
    private var isShowing = false
    private val handler = Handler(Looper.getMainLooper())

    // Toast项数据类，添加onClick回调
    private data class ToastItem(
        val message: String,
        val title: String? = null,
        val iconRes: Int? = null,
        val duration: Long = 3000L,
        val onClick: (() -> Unit)? = null  // 添加点击回调
    )

    // 添加onClick参数的toast方法
    fun toast(message: String, title: String? = null, icon: Int? = null, duration: Long = 1000L, onClick: (() -> Unit)? = null) {
        val toastItem = ToastItem(message, title, icon, duration, onClick)

        // 将Toast添加到队列
        toastQueue.offer(toastItem)

        // 尝试显示下一个Toast
        if (Looper.myLooper() == Looper.getMainLooper()) {
            // 当前在主线程
            showNextToastIfNeeded()
        } else {
            // 当前在子线程，使用Handler切换到主线程
            handler.post {
                showNextToastIfNeeded()
            }
        }
    }

    /**
     * 显示下一个Toast（如果当前没有正在显示的Toast）
     */
    private fun showNextToastIfNeeded() {
        if (!isShowing && !toastQueue.isEmpty()) {
            val toastItem = toastQueue.poll() ?: return
            isShowing = true

            showAppToast(
                message = toastItem.message,
                title = toastItem.title,
                iconRes = toastItem.iconRes,
                duration = toastItem.duration,
                onClick = toastItem.onClick  // 传递点击回调
            )
        }
    }

    /**
     * 当前Toast显示完成，准备显示下一个
     */
    private fun onToastFinished() {
        isShowing = false
        showNextToastIfNeeded()
    }

    // 修改showAppToast方法，添加onClick参数
    fun showAppToast(
        message: String,
        title: String? = null,
        iconRes: Int? = null,
        duration: Long = 3000L,
        onClick: (() -> Unit)? = null  // 添加点击回调
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

        // 使用自定义WindowManager显示，可以控制显示时长和点击事件
        showCustomToast(view, duration, onClick)
    }

    // 修改showCustomToast方法，添加onClick参数
    fun showCustomToast(toastView: View, duration: Long, onClick: (() -> Unit)? = null) {
        val context = appContext
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        // 设置LayoutParams
        val params = WindowManager.LayoutParams().apply {
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            }
            // 如果有点击事件，需要移除FLAG_NOT_TOUCHABLE
            flags = if (onClick != null) {
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            } else {
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            }
            format = PixelFormat.TRANSLUCENT
            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            y = 200
        }

        // 如果有点击事件，设置点击监听器
        if (onClick != null) {
            toastView.setOnClickListener {
                onClick.invoke()
                // 点击后移除Toast
                removeToastView(windowManager, toastView)
            }

            // 点击外部区域也可以移除
            toastView.isFocusable = true
            toastView.isClickable = true
        }

        // 添加到窗口
        windowManager.addView(toastView, params)

        // 使用Handler延迟移除
        handler.postDelayed({
            removeToastView(windowManager, toastView)
        }, duration)
    }

    /**
     * 移除Toast视图的辅助方法
     */
    private fun removeToastView(windowManager: WindowManager?, toastView: View) {
        try {
            windowManager?.removeView(toastView)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            // 无论移除是否成功，都标记当前Toast显示完成
            onToastFinished()
        }
    }
}