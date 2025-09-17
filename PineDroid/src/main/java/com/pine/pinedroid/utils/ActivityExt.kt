package com.pine.pinedroid.utils

import android.app.Activity
import android.content.Intent


val Activity.shortName: String
    get() {
        return this.javaClass.simpleName
    }

inline fun <reified T : Activity> pineActivityStart() {
    val intent = Intent(currentActivity, T::class.java)
    currentActivity.startActivity(intent)
}
