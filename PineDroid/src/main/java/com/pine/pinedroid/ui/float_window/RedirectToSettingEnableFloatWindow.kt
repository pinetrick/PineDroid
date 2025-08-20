package com.pine.pinedroid.ui.float_window

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.pine.pinedroid.utils.activityContext
import com.pine.pinedroid.utils.appContext
import kotlinx.coroutines.delay

object RedirectToSettingEnableFloatWindow {
    operator fun invoke(): Boolean{
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${appContext.packageName}")
        )
        // 如果 context 是 Activity，可以直接 startActivity
        // 如果 context 是 Application 或非 Activity，需要加 FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        appContext.startActivity(intent)
        return true
    }
}