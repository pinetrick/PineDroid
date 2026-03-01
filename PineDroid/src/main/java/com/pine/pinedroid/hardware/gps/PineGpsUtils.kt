package com.pine.pinedroid.hardware.gps

import android.graphics.Color
import kotlin.math.max
import kotlin.math.min


fun Double?.distanceToString(): String {
    if (this == null) return "Unknown"

    return when {
        this >= 1000 -> {
            // 保留2位小数显示公里
            String.format("%.2fkm", this / 1000)
        }
        else -> {
            // 米显示整数
            "${this.toInt()}m"
        }
    }
}

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

    private val slopeColorFlat    = Color.rgb(198, 227, 115)  // 黄绿  #C6E373
    private val slopeColorSteepUp = Color.rgb(183,  28,  28)  // 深红  #B71C1C
    private val slopeColorSteepDn = Color.rgb( 13,  71, 161)  // 深蓝  #0D47A1

    fun slopeToColor(slope: Float, minSlope: Float, maxSlope: Float): Int {
        return if (slope >= 0f) {
            val t = if (maxSlope > 0f) (slope / maxSlope).coerceIn(0f, 1f) else 0f
            interpolateColor(slopeColorFlat, slopeColorSteepUp, t)
        } else {
            val t = if (minSlope < 0f) (slope / minSlope).coerceIn(0f, 1f) else 0f
            interpolateColor(slopeColorFlat, slopeColorSteepDn, t)
        }
    }

    private fun interpolateColor(c1: Int, c2: Int, t: Float): Int {
        val r = (Color.red(c1)   + (Color.red(c2)   - Color.red(c1))   * t).toInt()
        val g = (Color.green(c1) + (Color.green(c2) - Color.green(c1)) * t).toInt()
        val b = (Color.blue(c1)  + (Color.blue(c2)  - Color.blue(c1))  * t).toInt()
        return Color.rgb(r, g, b)
    }

}