package com.pine.pinedroid.hardware.gps

import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.pine.pinedroid.utils.appContext
import com.pine.pinedroid.utils.log.loge
import com.pine.pinedroid.utils.log.logw

object MyLocation {

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(appContext)
    }


    @SuppressLint("MissingPermission")
    fun getCurrentLocation(
        onSuccess: (LatLng) -> Unit,
        onFailure: (Exception) -> Unit = {}
    ) {
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        val latLng = LatLng(it.latitude, it.longitude)
                        onSuccess(latLng)
                    } ?: run {
                        logw("无法获取位置信息")
                        onFailure(Exception("无法获取位置信息"))
                    }
                }
                .addOnFailureListener { exception ->
                    logw("MyError", exception)
                    onFailure(exception)
                }
        } catch (e: Exception) {
            logw("MyError", e)
            onFailure(e)
        }

    }

}