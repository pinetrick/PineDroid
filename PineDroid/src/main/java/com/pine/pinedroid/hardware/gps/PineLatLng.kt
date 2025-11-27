package com.pine.pinedroid.hardware.gps

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

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


}