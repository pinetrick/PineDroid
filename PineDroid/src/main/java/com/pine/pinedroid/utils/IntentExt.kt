package com.pine.pinedroid.utils

import android.content.ActivityNotFoundException
import android.content.Intent
import kotlin.reflect.KClass

fun intent(className: Class<*>) {
    try {
        val intent = Intent(activityContext, className)
        activityContext.startActivity(intent)
    }
    catch (e: ActivityNotFoundException) {
        toast("Do you reg this activity to AndroidManifest?")
    }
}

fun intent(className: KClass<*>) {
    return intent(className.java)
}
