package com.pine.pinedroid.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder

val gson: Gson by lazy {
    // 创建自定义的 Gson 实例
    GsonBuilder()
        .setPrettyPrinting()
        .setDateFormat("yyyy-MM-dd HH:mm:ss") // 匹配你的日期格式
        .create()

}