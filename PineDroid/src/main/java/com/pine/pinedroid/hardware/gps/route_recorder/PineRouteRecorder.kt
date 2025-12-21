package com.pine.pinedroid.hardware.gps.route_recorder

import com.pine.pinedroid.db.table
import com.pine.pinedroid.hardware.gps.PineLatLng
import com.pine.pinedroid.hardware.gps.PineLocationForegroundService
import com.pine.pinedroid.hardware.gps.PineLocationUpdateListener
import com.pine.pinedroid.utils.log.logd
import com.pine.pinedroid.utils.shrinker_keep.Keep
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

open class PineRouteRecorder internal constructor() : PineLocationUpdateListener {

    init {
        table<PineRecorderLatLngBean>().createTable()
        PineLocationForegroundService.subscribe(this)
    }

    fun recordCurrentLocation(){
        getLastLocation()?.let {
            onLocationChanged(it)
        }

    }

    fun getLastLocation() : PineLatLng? {
        return PineLocationForegroundService.getServiceInstance()?.getCurrentLocation()
    }

    override fun onLocationChanged(location: PineLatLng) {
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