package com.pine.pinedroid.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder

val gson: Gson by lazy {
    GsonBuilder().setPrettyPrinting().create()
}