package com.pine.pinedroid.hardware.permission.one_permission

import android.Manifest
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import com.pine.pinedroid.R
import com.pine.pinedroid.hardware.permission.PineOnePermission
import com.pine.pinedroid.ui.float_window.RedirectToSettingEnableFloatWindow
import com.pine.pinedroid.utils.appContext
import com.pine.pinedroid.utils.r_resource.stringResource

class PineOnePermissionAlertWindow(optional: Boolean = false) :
    PineOnePermission(
        permission = Manifest.permission.SYSTEM_ALERT_WINDOW,
        icon = "\uf2d2",
        text = R.string.pine_permission_system_alert_window_text.stringResource(),
        description = R.string.pine_permission_system_alert_window_description.stringResource(),
        optional = optional,
    ) {
    override fun hasPermission(): Boolean = Settings.canDrawOverlays(appContext)

    override fun isPermissionPermanentlyDenied() = false

    override fun rememberPermissionDeny() {}

    override fun requirePermission(requestPermissionLauncher: ActivityResultLauncher<String>) {
        RedirectToSettingEnableFloatWindow.invoke()
    }


}
