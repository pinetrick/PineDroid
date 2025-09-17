package com.pine.pinedroid.utils

import android.app.Activity
import android.content.Context
import androidx.activity.ComponentActivity

lateinit var activityContext: Context
lateinit var appContext: Context

val currentActivity: Activity get() = activityContext as Activity

val currentComponentActivity: ComponentActivity  get() = activityContext as ComponentActivity