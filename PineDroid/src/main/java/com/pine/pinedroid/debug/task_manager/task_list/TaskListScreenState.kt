package com.pine.pinedroid.debug.task_manager.task_list

import com.pine.pinedroid.utils.shrinker_keep.Keep

@Keep
data class TaskListScreenState(
    var apps: ArrayList<AppInfos> = ArrayList(),
    var memoryInfo: MemoryInfo = MemoryInfo(),
    var isLoading: Boolean = true,
    var processToKill: AppInfos? = null,
)
@Keep
data class AppInfos(
    val name: String = "",
    val packageName: String = "",
    val pss: String = "",
    val pid: String = "",
    val category: String = "",
    val isMainProcess: Boolean = false,
)
@Keep
data class MemoryInfo(
    var totalRAM: String = "",
    var freeRAM: String = "",
    var usedRAM: String = "",
    var lostRAM: String = "",
    var zramInfo: String = ""
)