package com.pine.pinedroid.hardware.step_counter

import android.app.PendingIntent
import android.content.Intent
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityRecognitionClient
import com.google.android.gms.location.DetectedActivity
import com.pine.pinedroid.utils.activityContext
import com.pine.pinedroid.utils.log.loge
import com.pine.pinedroid.utils.log.logi
import java.util.concurrent.CopyOnWriteArraySet

/**
 * Google Play Services 步行检测器（ActivityRecognitionClient）
 */
class PineWalkingDetector private constructor() {

    companion object {

        private const val DETECTION_INTERVAL_MS = 2_000L

        @Volatile
        private var instance: PineWalkingDetector? = null

        var lastPineMotionType: PineMotionType = PineMotionType.UNKNOWN

        fun isOnFoot(): Boolean {
            return lastPineMotionType in listOf(PineMotionType.WALKING, PineMotionType.RUNNING)
        }

        fun getInstance(): PineWalkingDetector {
            return instance ?: synchronized(this) {
                instance ?: PineWalkingDetector().also {
                    it.startDetection()
                    instance = it
                }
            }
        }

        internal fun onActivityChanged(type: Int, confidence: Int) {
            val motion = instance?.convertToMotionType(type) ?: return
            lastPineMotionType = motion
            instance?.listeners?.forEach {
                it.onMotionChanged(motion, confidence)
            }
        }
    }


    private val context = activityContext.applicationContext
    private val client: ActivityRecognitionClient =
        ActivityRecognition.getClient(context)

    private val listeners = CopyOnWriteArraySet<MotionListener>()

    private var isDetecting = false
    private var pendingIntent: PendingIntent? = null


    /* ================= Public API ================= */

    fun addListener(listener: MotionListener): PineWalkingDetector {
        if (!listeners.contains(listener))
            listeners.add(listener)
        return this
    }

    fun removeListener(listener: MotionListener) {
        listeners.remove(listener)
    }

    fun startDetection() {
        if (isDetecting) return

        val intent = Intent(context, ActivityRecognitionReceiver::class.java)

        pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        client.requestActivityUpdates(
            DETECTION_INTERVAL_MS,
            pendingIntent!!
        ).addOnSuccessListener {
            isDetecting = true
            logi("ActivityRecognition started")
        }.addOnFailureListener {
            loge("ActivityRecognition failed: ${it.message}")
        }
    }


    /* ================= Mapping ================= */

    private fun convertToMotionType(type: Int): PineMotionType {
        return when (type) {
            DetectedActivity.WALKING -> PineMotionType.WALKING
            DetectedActivity.RUNNING -> PineMotionType.RUNNING
            DetectedActivity.ON_FOOT -> PineMotionType.WALKING
            DetectedActivity.STILL -> PineMotionType.STILL
            DetectedActivity.IN_VEHICLE -> PineMotionType.VEHICLE
            DetectedActivity.ON_BICYCLE -> PineMotionType.BICYCLE
            else -> PineMotionType.UNKNOWN
        }
    }
}

enum class PineMotionType {
    STILL,
    WALKING,
    RUNNING,
    VEHICLE,
    BICYCLE,
    UNKNOWN
}