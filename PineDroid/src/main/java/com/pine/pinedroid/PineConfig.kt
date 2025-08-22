package com.pine.pinedroid

import com.pine.pinedroid.utils.PineApp
import com.pine.pinedroid.utils.sp

object PineConfig {
    private var isDebug: Boolean? = null
    private var isDebugWindowAlwaysOn: Boolean? = null

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


    fun setIsDebug(isDebug: Boolean) {
        sp("DebugMode", isDebug)
        refresh()
    }

    fun getIsDebug(): Boolean {
        if (isDebug == null) {
            refresh()
        }
        return isDebug!!
    }

    private fun refresh(){
        isDebug = true
        val spDbg: Boolean? = sp("DebugMode")
        if (spDbg != null) isDebug = spDbg
        else if (!PineApp.isAppDebug()) isDebug = false

        isDebugWindowAlwaysOn = true
        val spWindow: Boolean? = sp("DebugWindowAlwaysOn")
        if (spWindow != null) isDebugWindowAlwaysOn = spWindow
        else if (!PineApp.isAppDebug()) isDebugWindowAlwaysOn = false

    }

}