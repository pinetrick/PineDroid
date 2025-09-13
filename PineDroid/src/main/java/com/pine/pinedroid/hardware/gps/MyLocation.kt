//package com.pine.pinedroid.hardware.gps
//
//import android.annotation.SuppressLint
//import android.location.Location
//import android.os.Build
//import android.os.Looper
//import com.google.android.gms.location.FusedLocationProviderClient
//import com.google.android.gms.location.LocationCallback
//import com.google.android.gms.location.LocationRequest
//import com.google.android.gms.location.LocationResult
//import com.google.android.gms.location.LocationServices
//import com.google.android.gms.location.Priority
//import com.pine.pinedroid.utils.appContext
//import com.pine.pinedroid.utils.log.logw
//import java.lang.ref.WeakReference
//
//object MyLocation {
//
//    private val callbackFunctionList: ArrayList<WeakReference<(PineLatLng) -> Unit>> = arrayListOf()
//    private var isUpdating = false
//    private var locationCallback: LocationCallback? = null
//
//    private val fusedLocationClient: FusedLocationProviderClient by lazy {
//        LocationServices.getFusedLocationProviderClient(appContext)
//    }
//
//    @SuppressLint("MissingPermission")
//    fun getCurrentLocation(
//        onSuccess: (PineLatLng) -> Unit,
//        onFailure: (Exception) -> Unit = {}
//    ) {
//        try {
//            fusedLocationClient.lastLocation
//                .addOnSuccessListener { location: Location? ->
//                    location?.let {
//                        val latLng = PineLatLng(it.latitude, it.longitude, it.altitude, it.accuracy)
//                        onSuccess(latLng)
//                    } ?: run {
//                        logw("无法获取位置信息")
//                        onFailure(Exception("无法获取位置信息"))
//                    }
//                }
//                .addOnFailureListener { exception ->
//                    logw("MyError", exception)
//                    onFailure(exception)
//                }
//        } catch (e: Exception) {
//            logw("MyError", e)
//            onFailure(e)
//        }
//    }
//
//
//    fun startLocationUpdate(){
//        if (isUpdating) return
//        val locationRequest = createLocationRequest()
//
//        locationCallback = object : LocationCallback() {
//            override fun onLocationResult(locationResult: LocationResult) {
//                locationResult.lastLocation?.let { location ->
//                    val latLng = PineLatLng(
//                        location.latitude,
//                        location.longitude,
//                        location.altitude,
//                        location.accuracy
//                    )
//                    callbackFunctionList.removeAll { it.get() == null }
//                    callbackFunctionList.forEach { it.get()?.invoke(latLng) }
//                }
//            }
//        }
//
//        try {
//            fusedLocationClient.requestLocationUpdates(
//                locationRequest,
//                locationCallback!!,
//                Looper.getMainLooper()
//            )
//            isUpdating = true
//        } catch (e: SecurityException) {
//            logw("Location permission denied", e)
//        } catch (e: Exception) {
//            logw("Failed to request location updates", e)
//        }
//    }
//
//    fun subscribeLocationUpdates(onSuccess: (PineLatLng) -> Unit) {
//        callbackFunctionList.add(WeakReference(onSuccess))
//        startLocationUpdate()
//    }
//
//    private fun createLocationRequest(): LocationRequest {
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            // 新版本的方式（Android 12+）
//            LocationRequest.Builder(
//                Priority.PRIORITY_HIGH_ACCURACY,
//                10000L // 间隔时间10秒
//            ).apply {
//                setMinUpdateIntervalMillis(5000L) // 最快间隔5秒
//                setWaitForAccurateLocation(true)
//            }.build()
//        } else {
//            // 兼容旧版本的方式
//            @Suppress("DEPRECATION")
//            LocationRequest.create().apply {
//                interval = 10000 // 10秒
//                fastestInterval = 5000 // 5秒
//                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//            }
//        }
//    }
//
//    // 添加取消订阅的方法
//    fun unsubscribeLocationUpdates(locationCallback: LocationCallback? = null) {
//        if (locationCallback != null) {
//            fusedLocationClient.removeLocationUpdates(locationCallback)
//        }
//    }
//
//    // 添加一次性位置请求的方法
//    @SuppressLint("MissingPermission")
//    fun requestSingleLocationUpdate(
//        onSuccess: (PineLatLng) -> Unit,
//        onFailure: (Exception) -> Unit = {}
//    ) {
//        val locationRequest = createLocationRequest()
//
//        val locationCallback = object : LocationCallback() {
//            override fun onLocationResult(locationResult: LocationResult) {
//                locationResult.lastLocation?.let { location ->
//                    val latLng = PineLatLng(location.latitude, location.longitude, location.altitude, location.accuracy)
//                    onSuccess(latLng)
//                    // 获取到位置后立即取消订阅
//                    unsubscribeLocationUpdates(this)
//                }
//            }
//        }
//
//        try {
//            fusedLocationClient.requestLocationUpdates(
//                locationRequest,
//                locationCallback,
//                Looper.getMainLooper()
//            )
//        } catch (e: SecurityException) {
//            logw("Location permission denied", e)
//            onFailure(e)
//        } catch (e: Exception) {
//            logw("Failed to request location update", e)
//            onFailure(e)
//        }
//    }
//}