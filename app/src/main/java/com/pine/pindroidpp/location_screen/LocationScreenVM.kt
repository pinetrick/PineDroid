package com.pine.pindroidpp.location_screen


import com.pine.pinedroid.hardware.gps.route_recorder.PineRouteRecorder
import com.pine.pinedroid.jetpack.viewmodel.BaseViewModel


class LocationScreenVM : BaseViewModel<LocationScreenState>(LocationScreenState::class) {


    var pineRouteRecorder: PineRouteRecorder? = null
    fun locationRecordStart(){
        pineRouteRecorder = PineRouteRecorder()
    }
}