package com.pine.pinedroid.hardware

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.pine.pinedroid.utils.appContext

object BuildInfo {
    /**
     * 获取应用的版本号 (versionCode)
     * @param context 上下文对象
     * @return 版本号，如果获取失败则返回 -1
     */
    fun getBuildVersionNumber(): Long {
        return try {
            val packageInfo: PackageInfo = appContext.packageManager
                .getPackageInfo(appContext.packageName, 0)
            packageInfo.longVersionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            -1
        }
    }

    /**
     * 获取应用的版本名称 (versionName)
     * @param context 上下文对象
     * @return 版本名称，如果获取失败则返回空字符串
     */
    fun getBuildVersionName(): String? {
        return try {
            val packageInfo: PackageInfo = appContext.packageManager
                .getPackageInfo(appContext.packageName, 0)
            packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            ""
        }
    }

    /**
     * 获取应用的完整版本信息
     * @param context 上下文对象
     * @return 格式为 "版本名称 (版本号)" 的字符串
     */
    fun getFullVersionInfo(): String {
        return "${getBuildVersionName()} (${getBuildVersionNumber()})"
    }
}