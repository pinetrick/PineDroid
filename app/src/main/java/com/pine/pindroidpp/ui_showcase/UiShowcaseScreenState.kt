package com.pine.pindroidpp.ui_showcase

import com.pine.pinedroid.utils.shrinker_keep.Keep

@Keep
data class UiShowcaseScreenState(
    var rating: Float = 3.5f,
    var sliderValue: Float = 50f,
    var selectedTab: Int = 0,
    var searchText: String = "",
    var animatedTarget: Float? = null,
    var isLoggedIn: Boolean = false,
    var lastResult: String = "",
)
