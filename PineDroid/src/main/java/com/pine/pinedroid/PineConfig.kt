package com.pine.pinedroid

import com.pine.pinedroid.utils.PineApp
import com.pine.pinedroid.utils.sp

object PineConfig {
    private var isDebug: Boolean? = null


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


    }

}