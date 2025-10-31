package com.pine.pinedroid.debug.window

data class DebugWindowGridViewBean (val text: String, val icon: String = "", val action: () -> Unit = {})