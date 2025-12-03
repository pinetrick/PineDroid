package com.pine.pinedroid.debug.task_manager.task_list

import com.pine.pinedroid.utils.shrinker_keep.Keep

@Keep
data class TaskListScreenState(
    var apps: ArrayList<AppInfos> = ArrayList(),
    var memoryInfo: MemoryInfo = MemoryInfo(),
    var isLoading: Boolean = true
)
@Keep
data class AppInfos(
    val name: String = "",
    val pss: String = "",
    val pid: String = "",
    val category: String = ""
)
@Keep
data class MemoryInfo(
    var totalRAM: String = "",
    var freeRAM: String = "",
    var usedRAM: String = "",
    var lostRAM: String = "",
    var zramInfo: String = ""
)