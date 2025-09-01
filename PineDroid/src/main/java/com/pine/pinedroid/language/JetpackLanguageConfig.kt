package com.pine.pinedroid.language

import androidx.compose.runtime.mutableStateOf
import com.pine.pinedroid.utils.appContext
import java.util.Locale

val _appLocaleResource = mutableStateOf(appContext.resources)