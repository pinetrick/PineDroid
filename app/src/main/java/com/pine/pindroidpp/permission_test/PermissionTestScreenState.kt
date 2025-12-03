package com.pine.pindroidpp.permission_test

import com.pine.pinedroid.utils.shrinker_keep.Keep


@Keep
data class PermissionTestScreenState(
    var isLoading: Boolean = true,
    var hasPermission: Boolean = false,

){}