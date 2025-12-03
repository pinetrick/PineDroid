package com.pine.pinedroid.hardware.step_counter

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.pine.pinedroid.hardware.permission.PinePermissionUtils
import com.pine.pinedroid.hardware.permission.one_permission.PineOnePermissionActivityRecognition
import com.pine.pinedroid.utils.appContext
import com.pine.pinedroid.utils.log.logv
import com.pine.pinedroid.utils.log.logw
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


/**
 * 步数计数器工具类
 * 使用 Android 系统 Sensor.TYPE_STEP_COUNTER 传感器
 */
open class StepCounterHelper internal constructor() : SensorEventListener {
    private var sensorManager: SensorManager? = null
    private var stepCounterSensor: Sensor? = null

    /**
     * 检查设备是否支持计步传感器
     * @return 是否支持
     */
    var isStepCounterSupported: Boolean = false
        private set
    private var isInitialized = false


    /**
     * 初始化类并请求权限（使用 suspendCoroutine）
     * @return 权限是否被授予
     */
    suspend fun initClass(): Boolean = suspendCancellableCoroutine { continuation ->
        // 启动权限请求
        CoroutineScope(continuation.context).launch {
            try {
                PinePermissionUtils.requestPermissions(
                    listOf(
                        PineOnePermissionActivityRecognition(),
                    )
                ) { permissionsGranted ->
                    if (!permissionsGranted)  return@requestPermissions continuation.resume(false).also { logw("没有获取到权限") }
                    if (isInitialized) return@requestPermissions continuation.resume(true).also { logv("记步系统传感器存在") }

                    initSensor()
                    if (!isStepCounterSupported)  return@requestPermissions continuation.resume(false).also { logv("不支持的传感器") }

                    startCounting()
                    return@requestPermissions continuation.resume(true).also { logv("记步系统初始化成功") }
                }
            } catch (e: Exception) {
                // 如果发生异常，恢复协程并传递异常
                continuation.resumeWithException(e)
            }
        }
    }


    /**
     * 初始化传感器
     */
    private fun initSensor() {
        try {
            sensorManager = appContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager?
            if (sensorManager != null) {
                stepCounterSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
                this.isStepCounterSupported = (stepCounterSensor != null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            this.isStepCounterSupported = false
        }
    }

    /**
     * 开始监听步数变化
     * @return 是否成功开始监听
     */
    fun startCounting(): Boolean {
        if (!this.isStepCounterSupported || sensorManager == null) {
            return false
        }


        // 注册传感器监听
        val registered = sensorManager!!.registerListener(
            this,
            stepCounterSensor,
            SensorManager.SENSOR_DELAY_UI
        )

        if (registered) {
            isInitialized = true
        }

        return registered
    }

    /**
     * 停止监听步数变化
     */
    fun stopCounting() {
        if (sensorManager != null && isInitialized) {
            sensorManager!!.unregisterListener(this)
            isInitialized = false
        }
    }




    // ============= SensorEventListener 接口实现 =============
    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            val newTotalSteps = event.values[0].toInt()

            logv("StepCount", newTotalSteps)
//            // 获取最新的累计步数
//            val newTotalSteps = event.values[0].toInt()
//            totalStepsSinceBoot = newTotalSteps
//
//
//            // 如果是第一次收到数据，设置为基准值
//            if (bootStepCount == 0) {
//                bootStepCount = newTotalSteps
//            }
//
//
//            // 计算应用启动后新增的步数
//            val previousCurrentStepCount = currentStepCount
//            currentStepCount = newTotalSteps - bootStepCount
//
//
//            // 通知监听器
//            if (listener != null && currentStepCount != previousCurrentStepCount) {
//                listener!!.onStepCountChanged(totalStepsSinceBoot, currentStepCount)
//            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // 传感器精度变化，通常无需处理
    }



}