package com.pine.pinedroid.screen.permission.one_permission

import android.Manifest
import com.pine.pinedroid.R
import com.pine.pinedroid.screen.permission.PineOnePermission
import com.pine.pinedroid.utils.r_resource.stringResource

class PineOnePermissionReadMediaVideo(optional: Boolean = false) :
    PineOnePermission(
        permission = Manifest.permission.READ_MEDIA_VIDEO,
        icon = "\uf03d",
        text = R.string.pine_permission_read_media_video_text.stringResource(),
        description = R.string.pine_permission_read_media_video_description.stringResource(),
        optional = optional,
        requireApiLevel = 33..Int.MAX_VALUE,
    )