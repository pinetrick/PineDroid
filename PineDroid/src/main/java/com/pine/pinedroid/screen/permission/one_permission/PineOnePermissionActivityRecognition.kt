package com.pine.pinedroid.screen.permission.one_permission

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import com.pine.pinedroid.R
import com.pine.pinedroid.screen.permission.PineOnePermission
import com.pine.pinedroid.utils.r_resource.stringResource

//运动健康权限 计步
@RequiresApi(Build.VERSION_CODES.Q)
class PineOnePermissionActivityRecognition(optional: Boolean = false) :
    PineOnePermission(
        permission = Manifest.permission.ACTIVITY_RECOGNITION,
        icon = "\uf130",
        text = R.string.pine_permission_activity_recognition_text.stringResource(),
        description = R.string.pine_permission_activity_recognition_description.stringResource(),
        optional = optional,
    )