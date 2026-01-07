package com.pine.pinedroid.utils.r_resource

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalResources
import com.pine.pinedroid.isDebug
import com.pine.pinedroid.utils.appContext
import com.pine.pinedroid.utils.safeAppContext

fun Int.stringResource(vararg formatArgs: Any): String {
    return safeAppContext?.resources?.getString(this, *formatArgs)?:this.toString()
}

