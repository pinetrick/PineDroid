package com.pine.pinedroid.utils.r_resource

import com.pine.pinedroid.utils.appContext

fun Int.string() : String {
    return appContext.resources.getString(this)
}