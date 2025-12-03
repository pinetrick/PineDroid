package com.pine.pinedroid.hardware.step_counter

import com.pine.pinedroid.utils.log.loge

object PineStepCounter {
    var stepCounterHelper: StepCounterHelper? = null

    suspend fun initPineStepCounter(): Boolean {
        if (stepCounterHelper == null) {
            stepCounterHelper = StepCounterHelper()
        }

        return stepCounterHelper!!.initClass()
    }

    fun getStepSinceStartUp() : Long {
        if (stepCounterHelper == null) {
            loge("Please call initPineStepCounter before get steps")
        }

        return 0
    }

    fun getStepSinceAppInstalled() : Long {
        if (stepCounterHelper == null) {
            loge("Please call initPineStepCounter before get steps")
        }
        return 0
    }
}