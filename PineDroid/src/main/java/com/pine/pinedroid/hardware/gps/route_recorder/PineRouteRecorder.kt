package com.pine.pinedroid.hardware.gps.route_recorder

import com.pine.pinedroid.db.table
import com.pine.pinedroid.hardware.gps.PineLatLng
import com.pine.pinedroid.hardware.gps.PineLocationForegroundService

open class PineRouteRecorder {
    init {
        PineLocationForegroundService.Companion.subscribe(::onLocationChanged)
        table<PineRecorderLatLngBean>().createTable()
    }

    private fun onLocationChanged(location: PineLatLng) {
        location.toRecorderLatLngBean().save()
    }


}