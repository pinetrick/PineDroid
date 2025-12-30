package com.pine.pinedroid.hardware.gps

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.pine.pinedroid.BuildConfig
import com.pine.pinedroid.utils.appContext
import com.pine.pinedroid.utils.log.logd
import com.pine.pinedroid.utils.log.logi
import com.pine.pinedroid.utils.log.logv
import com.pine.pinedroid.utils.pineToString
import com.pine.pinedroid.utils.shrinker_keep.Keep
import com.pine.pinedroid.utils.toast
import java.lang.ref.WeakReference
import java.util.Date

@Keep
class PineLocationForegroundService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private val binder = LocalBinder()
    private var currentLocation: Location? = null
    private var lastValidAltitude: Double? = null


    inner class LocalBinder : Binder() {
        fun getService(): PineLocationForegroundService = this@PineLocationForegroundService
    }

    override fun onCreate() {
        super.onCreate()
        serviceInstance = WeakReference(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = createLocationRequest()
        createLocationCallback()

        fetchLastKnownLocationImmediately()
    }

    /**
     * 立即获取上次已知的位置（不等待）
     * 这个方法会在 Service 启动时调用，缓存上次的位置
     */
    private fun fetchLastKnownLocationImmediately() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            logv("No location permission to fetch last known location")
            return
        }

        // 立即获取上次已知位置（系统缓存的位置）
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                location?.let {
                    logv("Last known location cached: ${location.latitude}, ${location.longitude}")
                    currentLocation = location
                    notifyLocationChange(location)
                }
            }
            .addOnFailureListener { e ->
                logv("Failed to fetch last known location: ${e.message}")
            }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notification = buildNotification()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        startLocationUpdates()
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }



    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            "位置服务通道",
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)

    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("位置服务运行中")
            .setContentText("正在获取您的位置信息")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .build()
    }

    private fun createLocationRequest(): LocationRequest {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // 新版本的方式（Android 12+）
            LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                10000L // 间隔时间10秒
            ).apply {
                setMinUpdateIntervalMillis(5000L) // 最快间隔5秒
                setMaxUpdateDelayMillis(30000L) // 最大延迟30秒

                if (BuildConfig.DEBUG) {
                    setMinUpdateDistanceMeters(0f) // 即使没动也触发
                }
                else {
                    setMinUpdateDistanceMeters(2f)
                }
            }.build()
        } else {
            // 兼容旧版本的方式
            @Suppress("DEPRECATION")
            LocationRequest.create().apply {
                interval = 10000 // 10秒
                fastestInterval = 5000 // 5秒
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                smallestDisplacement = 1f
            }
        }
    }

    private fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                logv("onLocationChanged")
                locationResult.lastLocation?.let { location ->
                    notifyLocationChange(location)
                }
            }
        }
    }

    private fun notifyLocationChange(location: Location) {
        currentLocation = location
        val altitude = if (location.hasAltitude()) {
            lastValidAltitude = location.altitude
            location.altitude
        } else {
            lastValidAltitude ?: 0.0
        }


        val latLng = PineLatLng(
            location.latitude,
            location.longitude,
            altitude,
            location.accuracy,
            location.speed,
            dateTime = location.time

        )

        logv("Current Location", latLng.toDisplayString())

        callbackFunctionList.removeAll { it.get() == null }
        callbackFunctionList.forEach {
            it.get()?.onLocationChanged(latLng)
        }

    }

    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    fun getCurrentLocation(): PineLatLng? {
        return currentLocation?.let { currentLocation ->
            PineLatLng(
                currentLocation.latitude,
                currentLocation.longitude,
                currentLocation.altitude,
                currentLocation.accuracy,
                currentLocation.speed,
                currentLocation.time
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    companion object {
        const val CHANNEL_ID = "PineLocationForegroundServiceChannel"
        const val NOTIFICATION_ID = 56228466

        val callbackFunctionList: ArrayList<WeakReference<PineLocationUpdateListener>> = arrayListOf()


        private var serviceInstance: WeakReference<PineLocationForegroundService>? = null

        fun isServiceRunning(): Boolean {
            return serviceInstance?.get() != null
        }

        fun getServiceInstance(): PineLocationForegroundService? {
            return serviceInstance?.get()
        }

        // 启动服务的Intent
        private fun startService() {
            if (isServiceRunning()) return

            val intent = Intent(appContext, PineLocationForegroundService::class.java)
            appContext.startForegroundService(intent)
        }

        /**
         * 请注意 这个是弱引用，需要一个强引用这个函数确认生命周期
         * 请在调用处@keep那个变量
         */
        fun subscribe(onSuccess: PineLocationUpdateListener) {
            logd("subscribeLocationChange")
            callbackFunctionList.add(WeakReference(onSuccess))
            startService()
        }

        /**
         * 请注意 这个是弱引用，需要一个强引用这个函数确认生命周期
         */
        fun unsubscribe(onSuccess: PineLocationUpdateListener) {
            logd("unsubscribeLocationChange")
            callbackFunctionList.remove(WeakReference(onSuccess))
            startService()
        }
    }
}


