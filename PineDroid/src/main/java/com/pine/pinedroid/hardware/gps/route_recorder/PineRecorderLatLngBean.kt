package com.pine.pinedroid.hardware.gps.route_recorder

import com.pine.pinedroid.db.bean.BaseDataTable
import com.pine.pinedroid.hardware.gps.PineLatLng
import java.util.Date

data class PineRecorderLatLngBean(
    override var id: Long?,
    var lat: Double,
    var lng: Double,
    var altitude: Double,
    var accuracy: Float,
    var datetime: Date,
) : BaseDataTable() {
    fun toPineLatLng(): PineLatLng = PineLatLng(lat, lng, altitude, accuracy)

    override fun toString(): String {
        return "PineRecorderLatLngBean(lat=$lat, lng=$lng, altitude=$altitude, accuracy=$accuracy)"
    }
}

fun PineLatLng.toRecorderLatLngBean(): PineRecorderLatLngBean {
    return PineRecorderLatLngBean(null, lat, lng, altitude, accuracy, Date())
}