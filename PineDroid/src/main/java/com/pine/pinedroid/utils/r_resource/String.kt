package com.pine.pinedroid.utils.r_resource

import com.pine.pinedroid.utils.appContext

fun Int.stringResource(vararg formatArgs: Any): String {
    return appContext.resources.getString(this, *formatArgs)
}