package com.pine.pinedroid.screen.diagnostic

import com.pine.pinedroid.utils.shrinker_keep.Keep

@Keep
data class DiagnosticScreenState(
    // Memory
    val heapUsedMb: String = "--",
    val heapMaxMb: String = "--",
    val pssMb: String = "--",
    val ramAvailMb: String = "--",
    val ramTotalMb: String = "--",
    // CPU
    val cpuCores: String = "--",
    val cpuAbi: String = "--",
    // Network
    val netType: String = "--",
    val netConnected: String = "--",
    val netHasInternet: String = "--",
    // Storage
    val storageTotal: String = "--",
    val storageAvail: String = "--",
    val cacheSizeMb: String = "--",
    // Device
    val deviceModel: String = "--",
    val manufacturer: String = "--",
    val androidVersion: String = "--",
    val apiLevel: String = "--",
)
