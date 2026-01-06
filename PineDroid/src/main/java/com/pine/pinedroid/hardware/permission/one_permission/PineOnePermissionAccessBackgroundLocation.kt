package com.pine.pinedroid.hardware.permission.one_permission

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.pine.pinedroid.R
import com.pine.pinedroid.hardware.permission.PineOnePermission
import com.pine.pinedroid.utils.currentActivity
import com.pine.pinedroid.utils.r_resource.stringResource


class PineOnePermissionAccessBackgroundLocation(
    optional: Boolean = false
) : PineOnePermission(
    permission = Manifest.permission.ACCESS_BACKGROUND_LOCATION,
    icon = "\uf124",
    text = R.string.pine_permission_background_location_text.stringResource(),
    description = R.string.pine_permission_background_location_description.stringResource(),
    optional = optional,
    requireApiLevel = 29..Int.MAX_VALUE
) {
    override fun redirectToSettingAuth() {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", currentActivity.packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            currentActivity.startActivity(intent)
        } catch (e: Exception) {
            super.redirectToSettingAuth()
        }
    }
}