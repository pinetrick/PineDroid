package com.pine.pinedroid.hardware.gps

import com.pine.pinedroid.utils.shrinker_keep.Keep
import kotlinx.serialization.Serializable
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@Serializable
data class PineLatLng(
    var lat: Double,
    var lng: Double,
    var altitude: Double = 0.0,
    var accuracy: Float = 0.0f,
    var speedMeterPerSec: Float = 0.0f,
    var dateTime: Long = 0L,
) {
    /**
     * 计算当前点到另一个 PineLatLng 的距离（单位：米）
     */
    fun distanceTo(second: PineLatLng): Double {
        val earthRadius = 6371000.0 // 地球半径，单位：米

        val dLat = Math.toRadians(second.lat - lat)
        val dLng = Math.toRadians(second.lng - lng)

        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(lat)) * cos(Math.toRadians(second.lat)) *
                sin(dLng / 2).pow(2.0)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val surfaceDistance = earthRadius * c

//        logd("distance", surfaceDistance)
        return surfaceDistance
    }

    fun toDisplayString(): String {
        val latStr = String.format("%.6f", lat)
        val lngStr = String.format("%.6f", lng)

        val altitudeStr = if (altitude != 0.0) {
            " | 海拔 ${"%.1f".format(altitude)}m"
        } else ""

        val accuracyStr = if (accuracy > 0f) {
            " | 精度 ${"%.1f".format(accuracy)}m"
        } else ""

        val speedStr = if (speedMeterPerSec > 0f) {
            val kmh = speedMeterPerSec * 3.6f
            " | 速度 ${"%.1f".format(kmh)} km/h"
        } else ""

        val timeStr = if (dateTime > 0L) {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
            " | ${sdf.format(java.util.Date(dateTime))}"
        } else ""

        return "纬度 $latStr, 经度 $lngStr$altitudeStr$accuracyStr$speedStr$timeStr"
    }
}