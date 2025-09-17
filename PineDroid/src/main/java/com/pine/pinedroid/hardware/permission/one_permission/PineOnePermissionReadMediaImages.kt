package com.pine.pinedroid.hardware.permission.one_permission

import android.Manifest
import com.pine.pinedroid.R
import com.pine.pinedroid.hardware.permission.PineOnePermission
import com.pine.pinedroid.utils.r_resource.stringResource

class PineOnePermissionReadMediaImages(optional: Boolean = false) :
    PineOnePermission(
        permission = Manifest.permission.READ_MEDIA_IMAGES,
        icon = "\uf15b",
        text = R.string.pine_permission_read_media_images_text.stringResource(),
        description = R.string.pine_permission_read_media_images_description.stringResource(),
        optional = optional,
        requireApiLevel = 33..Int.MAX_VALUE,
    )