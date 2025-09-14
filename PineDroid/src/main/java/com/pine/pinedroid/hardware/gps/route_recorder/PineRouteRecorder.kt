package com.pine.pinedroid.hardware.gps.route_recorder

import com.pine.pinedroid.db.table
import com.pine.pinedroid.hardware.gps.PineLatLng
import com.pine.pinedroid.hardware.gps.PineLocationForegroundService

open class PineRouteRecorder {
    init {
        table<PineRecorderLatLngBean>().createTable()
        PineLocationForegroundService.subscribe(::onLocationChanged)
    }

    private fun onLocationChanged(location: PineLatLng) {
        location.toRecorderLatLngBean().save()
    }


}