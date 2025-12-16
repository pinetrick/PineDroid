package com.pine.pinedroid.system

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import com.pine.pinedroid.utils.appContext

object PineClipboard {

    private fun getClipboardManager(): ClipboardManager {
        return appContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    // 基础功能
    fun copyText(text: String, label: String? = null) {
        getClipboardManager().setPrimaryClip(
            ClipData.newPlainText(label ?: "text", text)
        )
    }

    fun getText(): String? {
        return getClipboardManager().primaryClip?.let { clip ->
            if (clip.itemCount > 0) {
                clip.getItemAt(0).text?.toString()
            } else null
        }
    }

    // 扩展功能 - 使用Kotlin扩展函数
    fun String.copyToClipboard(label: String? = null) {
        copyText(this, label)
    }

    // 安全获取文本，避免空指针
    fun getTextSafely(default: String = ""): String {
        return getText() ?: default
    }

    // 检查并获取文本
    fun getTextIfAvailable(): String? {
        return if (hasText()) getText() else null
    }

    fun hasText(): Boolean {
        val manager = getClipboardManager()
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            manager.hasPrimaryClip() && manager.primaryClipDescription?.hasMimeType(
                ClipDescription.MIMETYPE_TEXT_PLAIN
            ) == true
        } else {
            manager.hasPrimaryClip() && manager.primaryClip?.itemCount ?: 0 > 0
        }
    }

    fun clear() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getClipboardManager().clearPrimaryClip()
        } else {
            copyText("")
        }
    }

    // 监听器管理
    private val listeners = mutableSetOf<ClipboardManager.OnPrimaryClipChangedListener>()

    /**
     * 添加监听器并自动管理生命周期
     */
    fun addClipboardListener(onChanged: () -> Unit): ClipboardManager.OnPrimaryClipChangedListener {
        val listener = ClipboardManager.OnPrimaryClipChangedListener { onChanged() }
        addPrimaryClipChangedListener(listener)
        listeners.add(listener)
        return listener
    }

    /**
     * 移除所有已添加的监听器
     */
    fun removeAllListeners() {
        listeners.forEach { removePrimaryClipChangedListener(it) }
        listeners.clear()
    }

    private fun addPrimaryClipChangedListener(listener: ClipboardManager.OnPrimaryClipChangedListener) {
        getClipboardManager().addPrimaryClipChangedListener(listener)
    }

    private fun removePrimaryClipChangedListener(listener: ClipboardManager.OnPrimaryClipChangedListener) {
        getClipboardManager().removePrimaryClipChangedListener(listener)
    }
}

// 扩展函数，可以用于任何字符串
fun String.copyToClipboard(label: String? = null) {
    PineClipboard.copyText(this, label)
}