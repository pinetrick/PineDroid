package com.pine.pinedroid.screen.permission

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.pine.pinedroid.utils.appContext
import com.pine.pinedroid.utils.currentActivity
import com.pine.pinedroid.utils.safeAppContext
import com.pine.pinedroid.utils.sp


open class PineOnePermission(
    open val permission: String,
    open val icon: String = "\uf3c5",
    open val text: String = "",
    open val description: String = "",
    open val optional: Boolean = false,
    open val requireApiLevel: IntRange = 0..Int.MAX_VALUE,
) {
    open fun hasPermission(): Boolean {
        if (safeAppContext == null) return false

        return ContextCompat.checkSelfPermission(
            appContext,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    open fun isPermissionPermanentlyDenied(): Boolean {
        // 用户没同意 且 也不给 rationale（说明勾选了“不再询问”）
        return !hasPermission() &&
                !ActivityCompat.shouldShowRequestPermissionRationale(currentActivity, permission)
                && (sp<Boolean>("PermissionDenyBefore_$permission") == true)
    }

    open fun rememberPermissionDeny() {
        sp("PermissionDenyBefore_$permission", true)
    }

    open fun requirePermission(requestPermissionLauncher: ActivityResultLauncher<String>){
        requestPermissionLauncher.launch(permission)
    }

    open fun redirectToSettingAuth() {
        try {
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", appContext.packageName, null)
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            currentActivity.startActivity(intent)
        } catch (e: Exception) {
            // 兜底：如果某些 ROM 不支持，跳到通用设置页
            val intent = Intent(Settings.ACTION_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            currentActivity.startActivity(intent)
        }
    }

}






