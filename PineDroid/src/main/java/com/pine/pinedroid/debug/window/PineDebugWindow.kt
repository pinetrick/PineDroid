package com.pine.pinedroid.debug.window

import androidx.compose.ui.graphics.Paint
import com.pine.pinedroid.PineConfig
import com.pine.pinedroid.utils.PineApp
import com.pine.pinedroid.utils.sp

object PineDebugWindow {
    private var isDebugWindowAlwaysOn: Boolean? = null
    var buttons: LinkedHashMap<String, () -> Unit> = linkedMapOf()


    fun addButton(text: String, action: () -> Unit) {
        buttons[text] = action
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

    private  fun  refresh(){

        isDebugWindowAlwaysOn = true
        val spWindow: Boolean? = sp("DebugWindowAlwaysOn")
        if (spWindow != null) isDebugWindowAlwaysOn = spWindow
        else if (!PineApp.isAppDebug()) isDebugWindowAlwaysOn = false
    }

}