package com.pine.pinedroid.hardware.permission.one_permission

import android.Manifest
import com.pine.pinedroid.R
import com.pine.pinedroid.hardware.permission.PineOnePermission
import com.pine.pinedroid.utils.r_resource.stringResource

class PineOnePermissionWriteExternalStorage(optional: Boolean = false) :
    PineOnePermission(
        permission = Manifest.permission.WRITE_EXTERNAL_STORAGE,
        icon = "\uf31c",
        text = R.string.pine_permission_write_external_storage_text.stringResource(),
        description = R.string.pine_permission_write_external_storage_description.stringResource(),
        optional = optional,
        requireApiLevel = 0..32,
    )