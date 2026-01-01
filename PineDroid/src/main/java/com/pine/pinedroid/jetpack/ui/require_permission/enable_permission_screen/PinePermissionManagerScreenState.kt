package com.pine.pinedroid.jetpack.ui.require_permission.enable_permission_screen

import com.pine.pinedroid.hardware.permission.PineOnePermission
import com.pine.pinedroid.utils.shrinker_keep.Keep


@Keep
data class PinePermissionManagerScreenState(
    var permissionList: List<PermissionItem> = emptyList(),
)

@Keep
data class PermissionItem(
    val granted: Boolean,
    val permission: PineOnePermission
)