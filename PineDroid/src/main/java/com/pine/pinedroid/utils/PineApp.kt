package com.pine.pinedroid.utils

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import java.security.MessageDigest

object PineApp {
    fun isAppDebug(): Boolean {
        return try {
            (appContext.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        } catch (e: Exception) {
            false
        }
    }

    @SuppressLint("PackageManagerGetSignatures")
    fun printSignatures() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val info = appContext.packageManager.getPackageInfo(
                appContext.packageName,
                PackageManager.GET_SIGNING_CERTIFICATES
            )
            val signatures = info.signingInfo?.apkContentsSigners ?: return
            for (sig in signatures) {
                val md = MessageDigest.getInstance("SHA-1")
                val digest = md.digest(sig.toByteArray())
                val sha1 = digest.joinToString(":") { "%02X".format(it) }
                logi("AppSignature", "SHA-1: $sha1")
            }
        }
    }
}