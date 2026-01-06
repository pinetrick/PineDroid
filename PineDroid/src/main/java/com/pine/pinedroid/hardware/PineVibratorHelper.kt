package com.pine.pinedroid.hardware

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import com.pine.pinedroid.utils.appContext

/**
 * 通用的振动（触觉反馈）辅助类。
 * 封装了获取服务、权限检查以及多种振动效果的播放逻辑。
 * * 使用前请确保在 AndroidManifest.xml 中添加了权限:
 */
object PineVibratorHelper {



    // 使用 lazy 确保只有在第一次访问时才获取服务
    private val vibrator: Vibrator? by lazy {
        // 使用 applicationContext 以避免 Activity Context 内存泄漏风险
        val systemService = appContext.applicationContext.getSystemService(Context.VIBRATOR_SERVICE)
        systemService as? Vibrator
    }

    /**
     * 检查设备是否具有振动器硬件。
     */
    fun hasVibrator(): Boolean {
        return vibrator?.hasVibrator() ?: false
    }

    /**
     * 取消所有正在进行的振动。
     */
    fun cancel() {
        vibrator?.cancel()
    }

    /**
     * 播放一个通用的、短暂的点击反馈效果 (例如用于按钮按下)。
     * 优先使用系统预定义的 Haptic Effect，提供更优质的触觉体验。
     */
    fun performClickFeedback() {
        if (!hasVibrator()) {
            return
        }

        try {
            val v = vibrator!!

            // 推荐: 优先使用 EFFECT_CLICK (Android O/26+)
            if (v.areAllPrimitivesSupported(VibrationEffect.EFFECT_CLICK)) {
                val effect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                v.vibrate(effect)
            } else {
                // 回退方案: 简单的 OneShot 振动
                // 推荐使用 10ms 左右的短促振动作为点击反馈
                val fallbackEffect = VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE)
                v.vibrate(fallbackEffect)
            }
        } catch (e: Exception) {
        }
    }

    fun vibrate(duration: Long, repeat: Int = -1) {
        if (!hasVibrator()) {
            return
        }


        try {
            val waveformEffect = VibrationEffect.createWaveform(longArrayOf(0, duration), repeat)
            vibrator?.vibrate(waveformEffect)
        } catch (e: Exception) {
        }
    }

    /**
     * 播放一个自定义波形振动。
     * * @param pattern 一个 long 数组，定义了 [等待毫秒, 振动毫秒, 暂停毫秒, 振动毫秒, ...]
     * @param repeat 振动重复的索引 (-1 为不重复, 0 为从头开始重复)
     */
    fun vibratePattern(pattern: LongArray, repeat: Int = -1) {
        if (!hasVibrator()) {
            return
        }

        if (pattern.isEmpty()) return

        try {
            val waveformEffect = VibrationEffect.createWaveform(pattern, repeat)
            vibrator?.vibrate(waveformEffect)
        } catch (e: Exception) {
        }
    }

    /**
     * 播放一个简单的一次性振动。
     * * @param milliseconds 振动持续时间 ( 毫秒) 10~20ms 在 Doze / 屏幕灭时容易被忽略
     * @param amplitude 振动强度 (0-255，默认为 DEFAULT_AMPLITUDE)
     */
    fun vibrateOneShot(milliseconds: Long, amplitude: Int = VibrationEffect.DEFAULT_AMPLITUDE) {
        if (!hasVibrator()) {
            return
        }

        try {
            val oneShotEffect = VibrationEffect.createOneShot(milliseconds, amplitude)
            vibrator?.vibrate(oneShotEffect)
        } catch (e: Exception) {
        }
    }
}