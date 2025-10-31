package com.pine.pinedroid.hardware.gps.route_recorder

import com.pine.pinedroid.db.table
import com.pine.pinedroid.hardware.gps.PineLatLng
import com.pine.pinedroid.hardware.gps.PineLocationForegroundService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

open class PineRouteRecorder internal constructor() {

    //必须有强引用，否则会被gc
    private val locationCallback: (PineLatLng) -> Unit = ::onLocationChanged
    private var lastRecord: PineLatLng? = null

    init {
        table<PineRecorderLatLngBean>().createTable()
        PineLocationForegroundService.subscribe(locationCallback)
    }

    fun getLastLocation() : PineLatLng? {
        return lastRecord
    }

    private fun onLocationChanged(location: PineLatLng) {
        lastRecord = location
        location.toRecorderLatLngBean().save()

        _navEvents.value = location
    }


    companion object {
        private val _navEvents = MutableStateFlow<PineLatLng?>(null)
        val navEvents: StateFlow<PineLatLng?> = _navEvents

        var pineRouteRecorder: PineRouteRecorder? = null

        fun i(): PineRouteRecorder {
            if (pineRouteRecorder == null) {
                pineRouteRecorder = PineRouteRecorder()
            }
            return pineRouteRecorder!!
        }

    }
}