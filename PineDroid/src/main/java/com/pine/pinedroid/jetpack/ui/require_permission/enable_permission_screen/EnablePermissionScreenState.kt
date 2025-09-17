package com.pine.pinedroid.jetpack.ui.require_permission.enable_permission_screen


/**
 * 如果是true 代表已获取这个权限
 * 如果false 代表未获取这个权限
 * 如果是null 代表这个应用不需要这个权限 在UI可以隐藏
 */
data class EnablePermissionScreenState(
    var isCoarseLocation: Boolean? = false,

    var isLocation: Boolean? = true,
    var isLocationBackground: Boolean? = false,
    var byPassBatteryOptimization: Boolean? = false,

    var accessCamera: Boolean? = true,
    var recordAudio: Boolean? = true,
    var accessGallery: Boolean? = true,

    var readMediaImages: Boolean? = true,
    var readMediaVideo: Boolean? = true,
    var readMediaAudio: Boolean? = true,
    var bodySensors: Boolean? = true,

    var overlayPermission: Boolean? = true,
    var writeSettings: Boolean? = true,
)