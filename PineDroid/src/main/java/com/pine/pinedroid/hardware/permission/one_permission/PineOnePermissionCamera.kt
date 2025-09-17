package com.pine.pinedroid.hardware.permission.one_permission

import android.Manifest
import com.pine.pinedroid.R
import com.pine.pinedroid.hardware.permission.PineOnePermission
import com.pine.pinedroid.utils.r_resource.stringResource

class PineOnePermissionCamera(optional: Boolean = false) :
    PineOnePermission(
        permission = Manifest.permission.CAMERA,
        icon = "\uf030",
        text = R.string.pine_permission_camera_text.stringResource(),
        description = R.string.pine_permission_camera_description.stringResource(),
        optional = optional,
    )