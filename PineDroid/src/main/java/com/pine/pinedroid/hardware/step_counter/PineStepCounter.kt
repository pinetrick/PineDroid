package com.pine.pinedroid.hardware.step_counter

import com.pine.pinedroid.utils.log.loge
import com.pine.pinedroid.utils.shrinker_keep.Keep
import com.pine.pinedroid.utils.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


// 简化的监听器接口
interface StepCountListener {
    fun onStepCountChanged(stepSnapshot: StepSnapshot)
}

class PineStepCounter : StepCounterListener {
    lateinit var stepCounterHelper: StepCounterHelper

    val hasPermission: Boolean get() = stepCounterHelper.isInitialized

    // 设备启动以来的步数（直接使用系统返回的总步数）
    val stepSinceMobileStartUp: Int get() = stepCounterHelper.getTotalSteps()

    // 应用启动以来的步数（使用相对步数）
    var stepSinceAppStartUp: Int = 0

    // 应用安装以来的步数（需要持久化存储）
    private var _stepSinceAppInstalled: Int = 0
    val stepSinceAppInstalled: Int
        get() = _stepSinceAppInstalled

    // 今日步数（需要根据日期判断）
    private var _todayStep: Int = 0
    val todayStep: Int
        get() = _todayStep

    // 今日日期（用于判断是否需要重置今日步数）
    private var _todayDate: String = ""
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())


    private val listeners = mutableSetOf<StepCountListener>()

    /**
     * 初始化计步器
     */
    fun init(): PineStepCounter {
        // 加载持久化数据
        loadPersistedData()

        // 检查并重置今日步数（如果日期已变化）
        checkAndResetTodayStep()

        // 初始化计步器Helper
        stepCounterHelper = StepCounterHelper.getInstance()
        stepCounterHelper.initialize {
            stepCounterHelper.addListener(this)
            stepCounterHelper.startCounting()
        }


        return this
    }

    override fun onStepsUpdated(
        totalSteps: Int,
        stepDelta: Int,
        timestamp: Long
    ) {
        // 检查步数是否有效
        if (stepDelta <= 0 || totalSteps <= 0) {
            return
        }

        // 防止步数异常增加（比如传感器错误导致步数暴增）
        if (stepDelta > 1000) {
            loge("StepCounter", "检测到异常步数增加: $stepDelta，忽略此次更新")
            return
        }

        // 检查是否需要重置今日步数
        checkAndResetTodayStep()

        // 更新步数统计
        stepSinceAppStartUp += stepDelta
        _todayStep += stepDelta

        // 每次步数更新时，更新应用安装以来的总步数
        updateStepSinceInstalled(stepDelta)

        // 保存今日步数到持久化存储
        saveTodayStep()

        notifyListeners()
        // 可选：这里可以添加其他业务逻辑，如通知UI更新等
    }

    override fun onSensorStatusChanged(available: Boolean) {
        // 传感器状态变化处理
        if (!available) {
            loge("StepCounter", "计步传感器变为不可用")
            // 可以在这里添加传感器不可用时的处理逻辑
        }
    }

    /**
     * 检查并重置今日步数（如果日期已变化）
     */
    private fun checkAndResetTodayStep() {
        try {
            val currentDate = getCurrentDate()

            if (_todayDate.isEmpty()) {
                // 第一次加载，从持久化存储中读取今日步数
                loadTodayStep()
                _todayDate = currentDate
            } else if (_todayDate != currentDate) {
                // 日期已变化，重置今日步数
                loge("StepCounter", "日期变化：$_todayDate -> $currentDate，重置今日步数")
                _todayDate = currentDate
                _todayStep = 0
                saveTodayStep()
            }
        } catch (e: Exception) {
            loge("StepCounter", "检查重置今日步数失败: ${e.message}")
        }
    }

    /**
     * 获取当前日期字符串
     */
    private fun getCurrentDate(): String {
        return dateFormat.format(Date())
    }

    /**
     * 加载持久化数据
     */
    private fun loadPersistedData() {
        try {
            // 加载应用安装以来的步数
            _stepSinceAppInstalled = sp<Int>(KEY_STEP_SINCE_INSTALLED) ?: 0

            // 加载今日日期
            _todayDate = sp<String>(KEY_TODAY_DATE) ?: ""

        } catch (e: Exception) {
            loge("StepCounter", "加载持久化数据失败")
        }
    }

    /**
     * 加载今日步数
     */
    private fun loadTodayStep() {
        try {
            val savedDate = sp<String>(KEY_TODAY_DATE) ?: ""
            val currentDate = getCurrentDate()

            if (savedDate == currentDate) {
                // 如果是同一天，加载保存的步数
                _todayStep = sp<Int>(KEY_TODAY_STEP) ?: 0
            } else {
                // 日期不同，重置步数
                _todayStep = 0
                saveTodayStep()
            }
        } catch (e: Exception) {
            loge("StepCounter", "加载今日步数失败: ${e.message}")
            _todayStep = 0
        }
    }

    /**
     * 保存今日步数
     */
    @Synchronized
    private fun saveTodayStep() {
        try {
            _todayDate = getCurrentDate()
            sp(KEY_TODAY_DATE, _todayDate)
            sp(KEY_TODAY_STEP, _todayStep)
        } catch (e: Exception) {
            loge("StepCounter", "保存今日步数失败: ${e.message}")
        }
    }

    /**
     * 更新应用安装以来的步数并持久化
     */
    @Synchronized
    private fun updateStepSinceInstalled(newSteps: Int) {
        if (newSteps <= 0) return

        try {
            val oldValue = _stepSinceAppInstalled
            _stepSinceAppInstalled = oldValue + newSteps

            // 使用sp扩展函数保存数据
            sp(KEY_STEP_SINCE_INSTALLED, _stepSinceAppInstalled)

        } catch (e: Exception) {
            loge("StepCounter", "更新安装步数失败")
        }
    }

    /**
     * 手动更新今日步数（可用于从其他来源同步步数）
     */
    @Synchronized
    fun updateTodayStep(additionalSteps: Int) {
        if (additionalSteps <= 0) return

        try {
            // 检查是否需要重置今日步数
            checkAndResetTodayStep()

            _todayStep += additionalSteps
            saveTodayStep()

            // 同时更新应用安装以来的步数
            updateStepSinceInstalled(additionalSteps)

            // 更新应用启动步数（可选）
            stepSinceAppStartUp += additionalSteps

        } catch (e: Exception) {
            loge("StepCounter", "手动更新今日步数失败: ${e.message}")
        }
    }

    /**
     * 重置今日步数（例如用户手动重置）
     */
    @Synchronized
    fun resetTodayStep() {
        try {
            _todayStep = 0
            saveTodayStep()
            loge("StepCounter", "今日步数已重置")
        } catch (e: Exception) {
            loge("StepCounter", "重置今日步数失败: ${e.message}")
        }
    }

    /**
     * 获取今日步数的详细信息
     */
    fun getTodayStepInfo(): TodayStepInfo {
        return TodayStepInfo(
            steps = _todayStep,
            date = _todayDate,
            timestamp = System.currentTimeMillis()
        )
    }

    /**
     * 重置应用安装以来的步数
     */
    @Synchronized
    fun resetAppInstalledSteps() {
        try {
            _stepSinceAppInstalled = 0
            sp(KEY_STEP_SINCE_INSTALLED, _stepSinceAppInstalled)
            sp(KEY_LAST_SAVED_BASELINE, "null") // 删除保存的基准值
        } catch (e: Exception) {
            loge("StepCounter", "重置安装步数失败")
        }
    }

    /**
     * 清除所有步数数据
     */
    @Synchronized
    fun clearAllData() {
        try {
            resetAppInstalledSteps()
            resetTodayStep()

            // 清除其他相关数据
            sp(KEY_TODAY_DATE, "null")
            sp(KEY_TODAY_STEP, "null")
            sp(KEY_LAST_SAVED_BASELINE, "null")

            stepSinceAppStartUp = 0

            loge("StepCounter", "所有步数数据已清除")
        } catch (e: Exception) {
            loge("StepCounter", "清除所有数据失败: ${e.message}")
        }
    }

    /**
     * 获取当前所有步数统计的详细快照
     */
    fun getStepSnapshot(): StepSnapshot {
        // 确保今日步数是最新的
        checkAndResetTodayStep()

        return StepSnapshot(
            stepSinceMobileStartUp = stepSinceMobileStartUp,
            stepSinceAppStartUp = stepSinceAppStartUp,
            stepSinceAppInstalled = stepSinceAppInstalled,
            todayStep = _todayStep,
            hasPermission = hasPermission,
            timestamp = System.currentTimeMillis(),
        )
    }

    /**
     * 添加监听器
     */
    fun addListener(listener: StepCountListener): PineStepCounter {
        listeners.add(listener)
        // 立即通知当前状态
        listener.onStepCountChanged(getStepSnapshot())
        return this
    }

    /**
     * 移除监听器
     */
    fun removeListener(listener: StepCountListener) {
        listeners.remove(listener)
    }

    /**
     * 通知所有监听器
     */
    private fun notifyListeners() {
        listeners.forEach { it.onStepCountChanged(getStepSnapshot()) }
    }

    companion object {
        private const val KEY_STEP_SINCE_INSTALLED = "step_since_installed"
        private const val KEY_TODAY_STEP = "today_step"
        private const val KEY_TODAY_DATE = "today_date"
        private const val KEY_LAST_SAVED_BASELINE = "last_saved_baseline"

        private var instance: PineStepCounter? = null

        @JvmStatic
        // 请注意 第一次调用后需要调用init方法初始化
        fun getInstance(): PineStepCounter {
            return instance ?: synchronized(this) {
                instance ?: PineStepCounter().apply {
                    instance = this
                }
            }
        }

        /**
         * 销毁实例，释放资源
         */
        @Synchronized
        fun destroy() {
            instance?.stepCounterHelper?.removeAllListeners()
            instance = null
        }
    }
}

/**
 * 步数数据快照
 */
@Keep
data class StepSnapshot(
    val stepSinceMobileStartUp: Int,
    val stepSinceAppStartUp: Int,
    val stepSinceAppInstalled: Int,
    val todayStep: Int,
    val hasPermission: Boolean,
    val timestamp: Long,
)

/**
 * 今日步数信息
 */
@Keep
data class TodayStepInfo(
    val steps: Int,
    val date: String,
    val timestamp: Long
)