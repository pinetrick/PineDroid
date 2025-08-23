package com.pine.pinedroid.utils

import android.app.Activity


val Activity.shortName: String
    get() {
        return this.javaClass.simpleName
    }

