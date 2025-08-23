package com.pine.pinedroid.jetpack.viewmodel

sealed class NavEvent {
    data class Navigate(val route: String) : NavEvent()
    data class NavigateWithArgs(val route: String, val args: Map<String, Any> = emptyMap()) : NavEvent()
    object NavigateBack : NavEvent()
    data class PopUpTo(val route: String, val inclusive: Boolean = false) : NavEvent()
}