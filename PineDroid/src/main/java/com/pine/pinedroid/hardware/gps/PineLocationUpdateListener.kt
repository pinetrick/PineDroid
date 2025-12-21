package com.pine.pinedroid.hardware.gps


interface PineLocationUpdateListener {
    fun onLocationChanged(location: PineLatLng)
}