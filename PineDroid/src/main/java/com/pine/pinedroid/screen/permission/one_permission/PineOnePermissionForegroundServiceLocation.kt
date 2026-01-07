package com.pine.pinedroid.screen.permission.one_permission

import android.Manifest
import android.content.Intent
import android.provider.Settings
import com.pine.pinedroid.R
import com.pine.pinedroid.screen.permission.PineOnePermission
import com.pine.pinedroid.utils.currentActivity
import com.pine.pinedroid.utils.r_resource.stringResource

class PineOnePermissionForegroundServiceLocation(optional: Boolean = false) :
    PineOnePermission(
        permission = Manifest.permission.FOREGROUND_SERVICE_LOCATION,
        icon = "\uf124",
        text = R.string.pine_permission_foreground_service_location_text.stringResource(),
        description = R.string.pine_permission_foreground_service_location_description.stringResource(),
        optional = optional,
        requireApiLevel = 34..Int.MAX_VALUE
    ) {
    override fun redirectToSettingAuth() {
        try {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            currentActivity.startActivity(intent)
        } catch (e: Exception) {
            super.redirectToSettingAuth()
        }
    }
}