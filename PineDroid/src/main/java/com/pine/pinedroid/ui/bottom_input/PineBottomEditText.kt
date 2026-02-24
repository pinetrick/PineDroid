// PineBottomEditText.kt
package com.pine.pinedroid.ui.bottom_input

import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.pine.pinedroid.R
import com.pine.pinedroid.utils.currentActivity
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object PineBottomEditText {

    suspend fun show(
        defaultText: String = "",
        placeholder: String = "评论",
        textButton: String = "发送"
    ): String? = suspendCancellableCoroutine { continuation ->
        val activity = currentActivity

        val dialogView = LayoutInflater.from(activity).inflate(
            R.layout.pinedroid_bottom_edittext_dialog_bottom_input, null
        )

        val editText = dialogView.findViewById<EditText>(R.id.editText)
        val btnSend = dialogView.findViewById<Button>(R.id.btnSend)
        val backgroundDimmer = dialogView.findViewById<View>(R.id.backgroundDimmer)

        editText.hint = placeholder
        editText.setText(defaultText)
        editText.requestFocus()

        val dialog = AlertDialog.Builder(activity, R.style.BottomInputDialog)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        // 设置对话框从底部弹出
        val window = dialog.window
        window?.setGravity(android.view.Gravity.BOTTOM)

        // 设置宽度为全屏
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(window?.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        window?.attributes = layoutParams

        // 显示软键盘
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        btnSend.text = textButton

        // 发送按钮点击事件
        btnSend.setOnClickListener {
            val inputText = editText.text.toString()
            dialog.dismiss()
            continuation.resume(inputText)
        }

        // 取消按钮点击事件
        backgroundDimmer.setOnClickListener {
            dialog.dismiss()
            continuation.resume(null)
        }

        // 对话框取消时
        dialog.setOnDismissListener {
            if (continuation.isActive) {
                continuation.resume(null)
            }
        }

        // 协程取消时关闭对话框
        continuation.invokeOnCancellation {
            activity.runOnUiThread {
                if (dialog.isShowing) {
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }
}