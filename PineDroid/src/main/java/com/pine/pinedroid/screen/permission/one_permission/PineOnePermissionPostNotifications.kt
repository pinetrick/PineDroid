package com.pine.pinedroid.screen.permission.one_permission

import android.Manifest
import android.content.Intent
import android.provider.Settings
import com.pine.pinedroid.R
import com.pine.pinedroid.screen.permission.PineOnePermission
import com.pine.pinedroid.utils.currentActivity
import com.pine.pinedroid.utils.r_resource.stringResource

class PineOnePermissionPostNotifications(
    optional: Boolean = false
) : PineOnePermission(
    permission = Manifest.permission.POST_NOTIFICATIONS,
    icon = "\uf0f3",
    text = R.string.pine_permission_post_notifications_text.stringResource(),
    description = R.string.pine_permission_post_notifications_description.stringResource(),
    optional = optional,
    requireApiLevel = 33..Int.MAX_VALUE
) {
    override fun redirectToSettingAuth() {
        try {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, currentActivity.packageName)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            currentActivity.startActivity(intent)
        } catch (e: Exception) {
            // 回退到 App 详情页
            super.redirectToSettingAuth()
        }
    }
}
