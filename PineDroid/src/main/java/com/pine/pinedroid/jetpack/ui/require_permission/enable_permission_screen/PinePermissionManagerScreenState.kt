package com.pine.pinedroid.jetpack.ui.require_permission.enable_permission_screen

import com.pine.pinedroid.hardware.permission.PineOnePermission



data class PinePermissionManagerScreenState(
    var permissionList: List<PermissionItem> = emptyList(),
)

data class PermissionItem(
    val granted: Boolean,
    val permission: PineOnePermission
)