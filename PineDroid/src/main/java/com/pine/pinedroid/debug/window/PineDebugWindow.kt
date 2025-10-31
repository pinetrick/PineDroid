package com.pine.pinedroid.debug.window

import androidx.compose.ui.graphics.Paint
import com.pine.pinedroid.PineConfig
import com.pine.pinedroid.utils.PineApp
import com.pine.pinedroid.utils.sp

object PineDebugWindow {
    private var isDebugWindowAlwaysOn: Boolean? = null
    var buttons: ArrayList<DebugWindowGridViewBean> = ArrayList()

    fun addButton(text: String, icon: String = "", action: () -> Unit = {}) {
        // Check if a button with the same text already exists
        val existingButton = buttons.find { it.text == text }
        if (existingButton == null) {
            // Create new button and add to list
            val newButton = DebugWindowGridViewBean(text, icon, action)
            buttons.add(newButton)
        }
    }

    // --- 悬浮窗常显开关 ---
    fun setIsDebugWindowAlwaysOn(alwaysOn: Boolean) {
        sp("DebugWindowAlwaysOn", alwaysOn)
        refresh()
    }

    fun getIsDebugWindowAlwaysOn(): Boolean {
        if (isDebugWindowAlwaysOn == null) {
            refresh()
        }
        return isDebugWindowAlwaysOn!!
    }

    private fun refresh(){

        isDebugWindowAlwaysOn = true
        val spWindow: Boolean? = sp("DebugWindowAlwaysOn")
        if (spWindow != null) isDebugWindowAlwaysOn = spWindow
        else if (!PineApp.isAppDebug()) isDebugWindowAlwaysOn = false
    }

}