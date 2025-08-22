package com.pine.pinedroid.utils

import android.app.ActivityManager
import android.content.Context

object Memory {
    fun getAppMemoryUsage(): Int {
        val activityManager = appContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val pid = android.os.Process.myPid()
        val memoryInfoArray = activityManager.getProcessMemoryInfo(intArrayOf(pid))
        val memoryInfo = memoryInfoArray[0]

        // memoryInfo.totalPss 返回 KB
        val totalPssKb = memoryInfo.totalPss
        return totalPssKb  // 转成字节
    }


}