package com.pine.pinedroid.hardware.permission.one_permission

import android.Manifest
import com.pine.pinedroid.R
import com.pine.pinedroid.hardware.permission.PineOnePermission
import com.pine.pinedroid.utils.r_resource.stringResource

class PineOnePermissionReadMediaAudio(optional: Boolean = false) :
    PineOnePermission(
        permission = Manifest.permission.READ_MEDIA_AUDIO,
        icon = "\uf1c7",
        text = R.string.pine_permission_read_media_audio_text.stringResource(),
        description = R.string.pine_permission_read_media_audio_description.stringResource(),
        optional = optional,
        requireApiLevel = 33..Int.MAX_VALUE,
    )