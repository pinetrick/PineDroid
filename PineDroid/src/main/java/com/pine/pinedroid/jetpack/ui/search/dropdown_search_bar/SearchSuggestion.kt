package com.pine.pinedroid.jetpack.ui.search.dropdown_search_bar

import com.pine.pinedroid.activity.image_pickup.OneImage


data class SearchSuggestion(
    val title: String,
    val subtitle: String? = null,
    val type: String? = null,
    val icon: OneImage? = null
)
