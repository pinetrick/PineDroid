package com.pine.pinedroid.hardware.permission.one_permission

import android.Manifest
import android.content.Intent
import android.provider.Settings
import com.pine.pinedroid.R
import com.pine.pinedroid.hardware.permission.PineOnePermission
import com.pine.pinedroid.utils.currentActivity
import com.pine.pinedroid.utils.r_resource.stringResource

class PineOnePermissionCoarseLocation(optional: Boolean = false) :
    PineOnePermission(
        permission = Manifest.permission.ACCESS_COARSE_LOCATION,
        icon = "\uf21d",
        text = R.string.pine_permission_coarse_location_text.stringResource(),
        description = R.string.pine_permission_coarse_location_description.stringResource(),
        optional = optional,
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