package com.pine.pinedroid.screen.about

import com.pine.pinedroid.utils.shrinker_keep.Keep

@Keep
data class AboutScreenState(
    val versionCode: Int = 1,
    val versionName: String = "v1.0.0",
    val buildDate: String = "2024年12月",
    val minSdkVersion: String = "Android 8.0+",
    val appName: String = "Unknown",
){}