package com.pine.pinedroid.hardware.gps.route_recorder

import com.pine.pinedroid.db.bean.BaseDataTable
import com.pine.pinedroid.hardware.gps.PineLatLng
import com.pine.pinedroid.utils.pineToString
import com.pine.pinedroid.utils.sp
import java.util.Date
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

data class PineRecorderLatLngBean(
    override var id: Long?,
    var lat: Double,
    var lng: Double,
    var altitude: Double,
    var accuracy: Float,
    var speed: Float, //m/s
    var datetime: Date,
) : BaseDataTable() {
    fun toPineLatLng(): PineLatLng = PineLatLng(lat, lng, altitude, accuracy, speedMeterPerSec = speed, datetime.time)
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

    fun distanceTo(second: PineRecorderLatLngBean): Double {
        return distanceTo(second.toPineLatLng())
    }


    override fun toString(): String {
        return "PineRecorderLatLngBean(lat=$lat, lng=$lng, altitude=$altitude, accuracy=$accuracy)"
    }
}

fun PineLatLng.toRecorderLatLngBean(): PineRecorderLatLngBean {
    return PineRecorderLatLngBean(null, lat, lng, altitude, accuracy, speedMeterPerSec, Date(dateTime))
}