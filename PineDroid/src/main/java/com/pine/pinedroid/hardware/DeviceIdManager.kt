package com.pine.pinedroid.hardware

import android.annotation.SuppressLint
import android.provider.Settings
import com.pine.pinedroid.utils.appContext

object DeviceIdManager {
    /**
     * 获取Android ID作为设备标识符
     * 注意：恢复出厂设置或刷机会改变此值
     */
    @SuppressLint("HardwareIds")
    fun getAndroidId(): String {
        return Settings.Secure.getString(appContext.contentResolver, Settings.Secure.ANDROID_ID)
    }
}