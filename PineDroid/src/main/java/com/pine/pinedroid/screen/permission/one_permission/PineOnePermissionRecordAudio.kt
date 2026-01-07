package com.pine.pinedroid.screen.permission.one_permission

import android.Manifest
import com.pine.pinedroid.R
import com.pine.pinedroid.screen.permission.PineOnePermission
import com.pine.pinedroid.utils.r_resource.stringResource

class PineOnePermissionRecordAudio(optional: Boolean = false) :
    PineOnePermission(
        permission = Manifest.permission.RECORD_AUDIO,
        icon = "\uf130",
        text = R.string.pine_permission_record_audio_text.stringResource(),
        description = R.string.pine_permission_record_audio_description.stringResource(),
        optional = optional,
    )