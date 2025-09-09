package com.pine.pinedroid.jetpack.ui

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun PineIsScreenPortrait(): Boolean {
    val configuration = LocalConfiguration.current
    return (configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
}