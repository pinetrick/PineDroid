package com.pine.pinedroid.hardware.step_counter

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.SystemClock
import com.pine.pinedroid.hardware.permission.PinePermissionUtils
import com.pine.pinedroid.hardware.permission.one_permission.PineOnePermissionActivityRecognition
import com.pine.pinedroid.utils.appContext
import com.pine.pinedroid.utils.log.logv
import com.pine.pinedroid.utils.log.logw
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * 步数回调接口
 */
interface StepCounterListener {
    /**
     * 步数更新回调
     * @param totalSteps 设备启动以来的总步数（系统值）
     * @param stepDelta 距离上次回调变化的步数（本次回调相比上次回调增加的步数）
     * @param timestamp 时间戳（毫秒）
     */
    fun onStepsUpdated(totalSteps: Int, stepDelta: Int, timestamp: Long)

    /**
     * 传感器状态变化
     * @param available 传感器是否可用
     */
    fun onSensorStatusChanged(available: Boolean)
}

/**
 * 步数计数器工具类
 * 使用 Android 系统 Sensor.TYPE_STEP_COUNTER 传感器
 */
open class StepCounterHelper private constructor() : SensorEventListener {
    private var sensorManager: SensorManager? = null
    private var stepCounterSensor: Sensor? = null

    // 步数数据
    private var lastTotalSteps = 0 // 上次记录的总步数
    private var lastUpdateTime = 0L // 最后更新时间

    // 监听器
    private val listeners = mutableListOf<StepCounterListener>()

    // 状态标志
    var isStepCounterSupported: Boolean = false
        private set
    var isInitialized = false
        private set
    var isCounting = false
        private set

    companion object {
        // 单例实例
        @Volatile
        private var instance: StepCounterHelper? = null

        /**
         * 获取实例（单例模式） 请注意 第一次拿到实力 需要调用init函数
         */
        fun getInstance(): StepCounterHelper {
            return instance ?: synchronized(this) {
                instance ?: StepCounterHelper().also {
                    instance = it
                }
            }
        }
    }

    /**
     * 初始化传感器
     * @return 是否支持计步传感器
     */
    private fun initSensor(): Boolean {
        try {
            sensorManager = appContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager?
            if (sensorManager != null) {
                stepCounterSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
                isStepCounterSupported = (stepCounterSensor != null)

                // 通知监听器传感器状态
                notifySensorStatus(isStepCounterSupported)

                return isStepCounterSupported
            }
        } catch (e: Exception) {
            e.printStackTrace()
            isStepCounterSupported = false
            notifySensorStatus(false)
        }
        return false
    }

    /**
     * 初始化类并请求权限（使用 suspendCoroutine）
     * @return 权限是否被授予
     */
    fun initialize(onResult: (Boolean) -> Unit): StepCounterHelper {
        CoroutineScope(Dispatchers.Main).launch {
            PinePermissionUtils.requestPermissions(
                listOf(
                    PineOnePermissionActivityRecognition(),
                )
            ) { permissionsGranted ->
                if (!permissionsGranted) {
                    logw("StepCounter", "没有获取到活动识别权限")
                    onResult(false)
                    return@requestPermissions
                }

                if (isInitialized) {
                    logv("StepCounter", "已经初始化过")
                    onResult(true)
                    return@requestPermissions
                }

                // 初始化传感器
                if (!initSensor()) {
                    logv("StepCounter", "设备不支持计步传感器")
                    onResult(false)
                    return@requestPermissions
                }

                isInitialized = true
                logv("StepCounter", "计步系统初始化成功")
                onResult(true)
            }
        }
        return this
    }

    /**
     * 开始监听步数变化
     * @return 是否成功开始监听
     */
    fun startCounting(): Boolean {
        if (!isStepCounterSupported || sensorManager == null || !isInitialized) {
            logw("StepCounter", "无法开始计数：传感器不支持或未初始化")
            return false
        }

        // 重置上次步数记录，确保第一次回调的stepDelta是合理的
        lastTotalSteps = 0

        // 注册传感器监听
        val registered = sensorManager!!.registerListener(
            this,
            stepCounterSensor,
            SensorManager.SENSOR_DELAY_UI
        )

        if (registered) {
            isCounting = true
            logv("StepCounter", "开始步数计数")
        }

        return registered
    }

    /**
     * 停止监听步数变化
     */
    fun stopCounting() {
        if (sensorManager != null && isCounting) {
            sensorManager!!.unregisterListener(this)
            isCounting = false
            logv("StepCounter", "停止步数计数")
        }
    }

    /**
     * 重置步数计数器
     * 这将重置内部状态，下一次回调的stepDelta将基于新的起始点计算
     */
    fun resetCounter() {
        lastTotalSteps = 0
        lastUpdateTime = 0L
        logv("StepCounter", "重置步数计数器")
    }

    /**
     * 获取系统总步数
     * @return 系统总步数，如果没有数据返回-1
     */
    fun getTotalSteps(): Int {
        return if (lastTotalSteps > 0) lastTotalSteps else -1
    }

    /**
     * 获取最后更新时间
     */
    fun getLastUpdateTime(): Long = lastUpdateTime

    /**
     * 添加步数监听器
     */
    fun addListener(listener: StepCounterListener): StepCounterHelper {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
        return this
    }

    /**
     * 移除步数监听器
     */
    fun removeListener(listener: StepCounterListener) {
        listeners.remove(listener)
    }

    /**
     * 移除所有监听器
     */
    fun removeAllListeners() {
        listeners.clear()
    }

    /**
     * 清理资源
     */
    fun release() {
        stopCounting()
        removeAllListeners()
        isInitialized = false
        sensorManager = null
        stepCounterSensor = null
        resetCounter()
        logv("StepCounter", "已释放资源")
    }

    // ============= 私有方法 =============

    /**
     * 计算步数变化量
     */
    private fun calculateStepDelta(newTotalSteps: Int): Int {
        return if (lastTotalSteps > 0 && newTotalSteps >= lastTotalSteps) {
            newTotalSteps - lastTotalSteps
        } else {
            // 如果是第一次收到数据，或者系统步数重置（设备重启），变化量为0
            0
        }
    }

    /**
     * 通知监听器步数更新
     */
    private fun notifyListeners(totalSteps: Int, stepDelta: Int, timestamp: Long) {
        listeners.forEach { listener ->
            try {
                listener.onStepsUpdated(totalSteps, stepDelta, timestamp)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 通知监听器传感器状态变化
     */
    private fun notifySensorStatus(available: Boolean) {
        listeners.forEach { listener ->
            try {
                listener.onSensorStatusChanged(available)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // ============= SensorEventListener 接口实现 =============
    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            val newTotalSteps = event.values[0].toInt()
            val timestamp = System.currentTimeMillis()

            // 计算步数变化量
            val stepDelta = calculateStepDelta(newTotalSteps)

            // 记录数据
            lastTotalSteps = newTotalSteps
            lastUpdateTime = timestamp

            // 记录日志
            //logv("StepCounter", "总步数: $newTotalSteps, 变化步数: $stepDelta, 时间戳: $timestamp")

            // 通知监听器
            notifyListeners(newTotalSteps, stepDelta, timestamp)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // 传感器精度变化，可以记录日志
        logv("StepCounter", "传感器精度变化: $accuracy")
    }
}