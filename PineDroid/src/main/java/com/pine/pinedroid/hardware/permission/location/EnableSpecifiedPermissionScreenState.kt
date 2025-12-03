package com.pine.pinedroid.hardware.permission.location

import com.pine.pinedroid.utils.shrinker_keep.Keep

@Keep
data class EnableSpecifiedPermissionScreenState(
    val permission: String = "",
    val icon: String = "\uf3c5",
    val text: String = "权限说明",
    val description: String = "权限用途说明",
    val optional: Boolean = true,
    val isPermissionPermanentlyDenied: Boolean = false,
)