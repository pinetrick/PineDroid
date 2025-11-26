package com.pine.pinedroid.utils

fun Double.pineFormat(format: String = "%.1f"): String{
    return format.format(this)
}

fun Double.pineFormat(decimal: Int = 1): String{
    return this.pineFormat( "%." + decimal + "f")
}