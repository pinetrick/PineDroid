package com.pine.pinedroid.screen.permission.one_permission

import android.Manifest
import com.pine.pinedroid.R
import com.pine.pinedroid.screen.permission.PineOnePermission
import com.pine.pinedroid.utils.r_resource.stringResource

class PineOnePermissionReadExternalStorage(optional: Boolean = false) :
    PineOnePermission(
        permission = Manifest.permission.READ_EXTERNAL_STORAGE,
        icon = "\uf15b",
        text = R.string.pine_permission_read_external_storage_text.stringResource(),
        description = R.string.pine_permission_read_external_storage_description.stringResource(),
        optional = optional,
        requireApiLevel = 0..32,
    )