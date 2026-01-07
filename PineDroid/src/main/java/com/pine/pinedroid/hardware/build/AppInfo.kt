package com.pine.pinedroid.hardware.build

import android.content.pm.PackageManager
import android.os.Build
import com.pine.pinedroid.utils.appContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


object AppInfo {
    fun getVersionName(): String {
        return try {
            val packageInfo = appContext.packageManager.getPackageInfo(appContext.packageName, 0)
            packageInfo.versionName ?: "Unknown"
        } catch (e: Exception) {
            "Unknown"
        }
    }

    fun getVersionCode(): Int {
        return try {
            val packageInfo = appContext.packageManager.getPackageInfo(appContext.packageName, 0)
            packageInfo.versionCode
        } catch (e: Exception) {
            -1 // Return -1 or 0 as error indicator
        }
    }


    fun getAppName(): String {
        return try {
            val packageManager = appContext.packageManager
            val applicationInfo = packageManager.getApplicationInfo(appContext.packageName, 0)
            packageManager.getApplicationLabel(applicationInfo).toString()
        } catch (e: Exception) {
            // 兜底：从 AndroidManifest 中获取
            appContext.applicationInfo.loadLabel(appContext.packageManager).toString()
        }
    }


    fun getBuildDateFromApk(): Long {
        return try {
            val appInfo = appContext.packageManager.getApplicationInfo(
                appContext.packageName,
                PackageManager.GET_META_DATA
            )
            val apkFile = File(appInfo.sourceDir)
            apkFile.lastModified()
        } catch (e: Exception) {
            -1L
        }
    }

    // 返回 "yyyy.MM" 格式的编译时间（优先使用 BuildConfig，回退用 APK 的 lastUpdateTime/firstInstallTime）
    fun getBuildDate(): String {
        val timestamp = getBuildDateFromApk()
        if (timestamp == -1L) return "Unknown"

        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(Date(timestamp))
    }

    fun getMinSdkFromPackageInfo(): Int {
        try {
            val packageInfo = appContext.packageManager.getPackageInfo(
                appContext.packageName,
                PackageManager.GET_ACTIVITIES
            )
            val appInfo = packageInfo.applicationInfo
            appInfo?.let { return it.minSdkVersion  }
        } catch (e: Exception) {

        }

        try {
            val appInfo = appContext.packageManager.getApplicationInfo(
                appContext.packageName,
                PackageManager.GET_META_DATA
            )
            return appInfo.minSdkVersion
        } catch (e: Exception) {
        }

        return -1;
    }

    // 返回 "Android X.X+" 的可读最小 SDK（优先使用 BuildConfig）
    fun getMinSdkVersion(): String {
        return try {
            val minSdk = getMinSdkFromPackageInfo() // int，来自 gradle buildConfigField
            when (minSdk) {
                Build.VERSION_CODES.O -> "Android 8.0+"
                Build.VERSION_CODES.O_MR1 -> "Android 8.1+"
                Build.VERSION_CODES.P -> "Android 9.0+"
                Build.VERSION_CODES.Q -> "Android 10.0+"
                Build.VERSION_CODES.R -> "Android 11.0+"
                Build.VERSION_CODES.S -> "Android 12.0+"
                32 -> "Android 12.1+"         // S_V2 / API 32，若编译环境没有常量直接用数字
                Build.VERSION_CODES.TIRAMISU -> "Android 13.0+"
                34 -> "Android 14.0+"         // 若 SDK 常量不可用，用数值回退
                else -> "Android ${minSdk}+"
            }
        } catch (e: Throwable) {
            "Unknown"
        }
    }

}