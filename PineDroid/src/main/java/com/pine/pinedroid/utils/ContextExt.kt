package com.pine.pinedroid.utils

import android.app.Activity
import android.content.Context

lateinit var activityContext: Context
lateinit var appContext: Context

val currentActivity: Activity get() = activityContext as Activity