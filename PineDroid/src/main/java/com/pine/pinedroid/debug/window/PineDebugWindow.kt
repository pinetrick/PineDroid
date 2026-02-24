package com.pine.pinedroid.debug.window

import com.pine.pinedroid.debug.server.PineDebugServer
import com.pine.pinedroid.utils.PineApp
import com.pine.pinedroid.utils.sp

object PineDebugWindow {
    val isDebugEnabled: Boolean
        get() {
            if (PineApp.isAppDebug()) return true
            return sp<Boolean>("DebugWindowAlwaysOn") ?: false
        }

    var buttons: ArrayList<DebugWindowGridViewBean> = ArrayList()

    fun addButton(text: String, icon: String = "", action: () -> Unit = {}) {
        // Auto-start debug HTTP server on first button add (debug mode only)
        if (isDebugEnabled && !PineDebugServer.isRunning()) {
            Thread { PineDebugServer.start() }.start()
        }
        // Check if a button with the same text already exists
        val existingButton = buttons.find { it.text == text }
        if (existingButton == null) {
            // Create new button and add to list
            val newButton = DebugWindowGridViewBean(text, icon, action)
            buttons.add(0, newButton)
        }
    }

    // --- 悬浮窗常显开关 ---
    fun setIsDebugWindowAlwaysOn(alwaysOn: Boolean) {
        sp("DebugWindowAlwaysOn", alwaysOn)
    }


}