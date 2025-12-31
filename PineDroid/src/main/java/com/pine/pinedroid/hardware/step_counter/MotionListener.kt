package com.pine.pinedroid.hardware.step_counter

interface MotionListener {
    fun onMotionChanged(motionType: PineMotionType, confidence: Int)
}
