package com.pine.pinedroid.hardware.gps

object PineLocation {
    fun getMyLocation(): PineLatLng? {
        return PineLocationForegroundService.getServiceInstance()?.getCurrentLocation()
    }

    fun isCurrentLocationInRange(latLng: PineLatLng) : Boolean {
        val distance = getMyLocation()?.distanceTo(latLng) ?: return false
        return distance <= latLng.accuracy


    }
}