package com.pine.pinedroid.hardware.step_counter

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.DetectedActivity
import com.google.android.gms.location.ActivityRecognitionResult
import com.pine.pinedroid.utils.activityContext
import com.pine.pinedroid.utils.log.loge
import com.pine.pinedroid.utils.toast
import java.util.concurrent.TimeUnit

// 简化监听接口
interface MotionListener {
    fun onMotionChanged(motionType: PineWalkingDetector.MotionType)
}

/**
 * 极简步行检测器（ActivityRecognition 官方方式，动态注册 BroadcastReceiver）
 * ！！！！！！！！！！！！！！！！！！不好用 有空在修理
 */
class PineWalkingDetector private constructor() {

    companion object {
        private const val TAG = "PineWalkingDetector"
        private const val DETECTION_INTERVAL_SECONDS = 2L

        @Volatile
        private var instance: PineWalkingDetector? = null

        fun getInstance(): PineWalkingDetector {
            return instance ?: synchronized(this) {
                instance ?: PineWalkingDetector().also {
                    it.startDetection()
                    instance = it
                }
            }
        }
    }

    enum class MotionType {
        STILL,
        WALKING,
        RUNNING,
        VEHICLE,
        BICYCLE,
        UNKNOWN
    }

    private val activityClient = ActivityRecognition.getClient(activityContext)
    private var currentMotion = MotionType.STILL
    private var isDetecting = false
    private val listeners = mutableSetOf<MotionListener>()

    // 动态注册的 Receiver
    private var activityReceiver: BroadcastReceiver? = null

    // ================================
    // ActivityRecognition 生命周期
    // ================================

    fun startDetection() {
        if (isDetecting) return

        try {
            // 创建 PendingIntent，不指向具体 Receiver
            val intent = Intent(activityContext, ActivityRecognitionReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                activityContext,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )

            activityClient.requestActivityUpdates(
                TimeUnit.SECONDS.toMillis(DETECTION_INTERVAL_SECONDS),
                pendingIntent
            ).addOnSuccessListener {
                isDetecting = true
                loge(TAG, "Activity detection started")

                // 动态注册 Receiver
                activityReceiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        if (intent == null) return
                        if (!ActivityRecognitionResult.hasResult(intent)) return

                        val result = ActivityRecognitionResult.extractResult(intent) ?: return
                        val activity = result.mostProbableActivity
                        val type = activity.type
                        val confidence = activity.confidence

                        loge(TAG, "Detected activity: $type, confidence=$confidence")
                        updateMotion(convertToMotionType(type))
                    }
                }

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    activityContext.registerReceiver(
                        activityReceiver,
                        IntentFilter().apply {
                            addAction("com.google.android.gms.location.ACTIVITY_RECOGNITION_DATA")
                        },
                        Context.RECEIVER_NOT_EXPORTED // 添加这个标志
                    )
                } else {
                    // Android 8.0 以下版本不需要这个标志
                    activityContext.registerReceiver(
                        activityReceiver,
                        IntentFilter().apply {
                            addAction("com.google.android.gms.location.ACTIVITY_RECOGNITION_DATA")
                        }
                    )
                }

            }.addOnFailureListener {
                loge(TAG, "Activity detection failed: ${it.message}")
            }

        } catch (e: Exception) {
            loge(TAG, "startDetection exception: ${e.message}")
        }
    }

    fun stopDetection() {
        if (!isDetecting) return

        try {
            // 移除更新
            val intent = Intent(activityContext, ActivityRecognitionReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                activityContext,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
            activityClient.removeActivityUpdates(pendingIntent)
            isDetecting = false

            // 注销 Receiver
            activityReceiver?.let {
                try {
                    activityContext.unregisterReceiver(it)
                } catch (_: Exception) {}
            }
            activityReceiver = null

            loge(TAG, "Activity detection stopped")

        } catch (e: Exception) {
            loge(TAG, "stopDetection exception: ${e.message}")
        }
    }

    // ================================
    // 状态管理
    // ================================

    private fun updateMotion(newMotion: MotionType) {
        if (currentMotion == newMotion) return
        currentMotion = newMotion
        loge(TAG, "Motion changed: $newMotion")
        notifyListeners()
    }

    private fun convertToMotionType(type: Int): MotionType {
        return when (type) {
            DetectedActivity.WALKING -> MotionType.WALKING
            DetectedActivity.RUNNING -> MotionType.RUNNING
            DetectedActivity.ON_FOOT -> MotionType.WALKING
            DetectedActivity.STILL -> MotionType.STILL
            DetectedActivity.IN_VEHICLE -> MotionType.VEHICLE
            DetectedActivity.ON_BICYCLE -> MotionType.BICYCLE
            else -> MotionType.UNKNOWN
        }
    }

    // ================================
    // 对外 API
    // ================================

    fun getCurrentMotion(): MotionType = currentMotion

    fun isMovingOnFoot(): Boolean =
        currentMotion == MotionType.WALKING || currentMotion == MotionType.RUNNING

    fun addListener(listener: MotionListener): PineWalkingDetector {
        listeners.add(listener)
        listener.onMotionChanged(currentMotion)
        return this
    }

    fun removeListener(listener: MotionListener) {
        listeners.remove(listener)
    }

    private fun notifyListeners() {
        listeners.forEach {
            it.onMotionChanged(currentMotion)
        }
    }

    fun release() {
        stopDetection()
        listeners.clear()
        instance = null
    }
}

