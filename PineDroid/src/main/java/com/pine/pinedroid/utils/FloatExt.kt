package com.pine.pinedroid.utils

fun Float.pineFormat(format: String = "%.1f"): String{
    return this.toDouble().pineFormat(format)
}

fun Float.pineFormat(decimal: Int = 1): String{
    return this.toDouble().pineFormat(decimal)
}