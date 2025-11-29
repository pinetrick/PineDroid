package com.pine.pinedroid

import com.pine.pinedroid.utils.PineApp
import com.pine.pinedroid.utils.sp

val isDebug: Boolean
    get(){
        if (PineApp.isAppDebug()) return true
        return sp<Boolean>("DebugMode") ?: false
    }

object PineConfig {
    fun setIsDebug(isDebug: Boolean) {
        sp("DebugMode", isDebug)
    }
}