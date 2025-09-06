package com.pine.pinedroid.utils.r_resource

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalResources
import com.pine.pinedroid.utils.appContext

fun Int.stringResource(vararg formatArgs: Any): String {
    return appContext.resources.getString(this, *formatArgs)
}

