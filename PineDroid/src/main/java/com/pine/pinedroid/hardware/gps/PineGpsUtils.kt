package com.pine.pinedroid.hardware.gps

import android.graphics.Color
import kotlin.math.max
import kotlin.math.min

object PineGpsUtils {


    fun elevationToColor(
        elevation: Float,
        minValue: Float = 0f,
        maxValue: Float = 2000f
    ): Int {
        val h = min(max(elevation, minValue), maxValue)
        val normalized = (h - minValue) / (maxValue - minValue) // 0到1的范围

        return when {
            normalized <= 0.25f -> {
                // 绿色 -> 黄色: (0,255,0) -> (255,255,0)
                val ratio = normalized / 0.25f
                val r = (255 * ratio).toInt()
                val g = 255
                val b = 0
                Color.rgb(r, g, b)
            }
            normalized <= 0.5f -> {
                // 黄色 -> 橙色: (255,255,0) -> (255,165,0)
                val ratio = (normalized - 0.25f) / 0.25f
                val r = 255
                val g = (255 - 90 * ratio).toInt() // 255 -> 165
                val b = 0
                Color.rgb(r, g, b)
            }
            normalized <= 0.75f -> {
                // 橙色 -> 红色: (255,165,0) -> (255,0,0)
                val ratio = (normalized - 0.5f) / 0.25f
                val r = 255
                val g = (165 - 165 * ratio).toInt() // 165 -> 0
                val b = 0
                Color.rgb(r, g, b)
            }
            else -> {
                // 红色 -> 黑色: (255,0,0) -> (0,0,0)
                val ratio = (normalized - 0.75f) / 0.25f
                val r = (255 - 255 * ratio).toInt() // 255 -> 0
                val g = 0
                val b = 0
                Color.rgb(r, g, b)
            }
        }
    }

}