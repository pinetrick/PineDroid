package com.pine.pinedroid.debug.task_manager.task_list

import android.app.ActivityManager
import android.app.usage.StorageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Debug
import android.os.StatFs
import androidx.lifecycle.viewModelScope
import com.pine.pinedroid.jetpack.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

class TaskListScreenVM : BaseViewModel<TaskListScreenState>(TaskListScreenState::class) {

    private lateinit var activityManager: ActivityManager
    private lateinit var packageManager: PackageManager
    private lateinit var storageStatsManager: StorageStatsManager

    fun initialize(context: Context) {
        activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        packageManager = context.packageManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            storageStatsManager = context.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
        }
    }

    suspend fun onInit() {
        loadMemoryInfo()
    }

    fun onRefresh() = viewModelScope.launch {
        setState { copy(isLoading = true) }
        loadMemoryInfo()
    }

    private suspend fun loadMemoryInfo() {
        runCatching {
            withContext(Dispatchers.IO) {
                loadSystemMemoryInfo()
            }
            setState { copy(isLoading = false) }
        }.onFailure {
            setState { copy(isLoading = false) }
        }
    }

    private fun loadSystemMemoryInfo() {
        val apps = ArrayList<AppInfos>()
        val memoryInfo = MemoryInfo()

        // 获取系统内存信息
        val systemMemoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(systemMemoryInfo)

        // 设置内存信息
        memoryInfo.totalRAM = formatBytes(systemMemoryInfo.totalMem)
        memoryInfo.freeRAM = formatBytes(systemMemoryInfo.availMem)
        memoryInfo.usedRAM = formatBytes(systemMemoryInfo.totalMem - systemMemoryInfo.availMem)

        // 计算阈值内存（当系统需要开始杀死进程时的内存）
        memoryInfo.lostRAM = formatBytes(systemMemoryInfo.threshold)

        // ZRAM 信息需要特殊处理，这里简化处理
        memoryInfo.zramInfo = "N/A"

        // 获取运行中的进程
        val runningProcesses = activityManager.runningAppProcesses ?: emptyList()

        for (processInfo in runningProcesses) {
            try {
                val processMemoryInfo = activityManager.getProcessMemoryInfo(intArrayOf(processInfo.pid))
                if (processMemoryInfo.isNotEmpty()) {
                    val memory = processMemoryInfo[0]
                    val pssInKb = memory.totalPss // 获取 PSS 内存（以 KB 为单位）

                    // 获取进程名称
                    val processName = processInfo.processName
                    val appName = getAppNameFromPackage(processName)

                    // 判断进程类型
                    val category = if (processName.contains(":")) {
                        "Service"
                    } else {
                        "Activity"
                    }

                    apps.add(
                        AppInfos(
                            name = appName,
                            pss = "${pssInKb}K",
                            pid = processInfo.pid.toString(),
                            category = category
                        )
                    )
                }
            } catch (e: Exception) {
                // 忽略无法获取内存信息的进程
                continue
            }
        }

        // 按内存使用量排序
        apps.sortByDescending { it.pss.replace("K", "").toIntOrNull() ?: 0 }

        setState {
            copy(
                apps = apps,
                memoryInfo = memoryInfo,
                isLoading = false
            )
        }
    }

    private fun getAppNameFromPackage(packageName: String): String {
        return try {
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(applicationInfo).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            packageName
        }
    }

    private fun formatBytes(bytes: Long): String {
        val units = arrayOf("B", "KB", "MB", "GB")
        var size = bytes.toDouble()
        var unitIndex = 0

        while (size > 1024 && unitIndex < units.size - 1) {
            size /= 1024
            unitIndex++
        }

        return "%.2f%s".format(size, units[unitIndex])
    }
}

// 辅助扩展函数，用于格式化内存显示
private fun Long.toFormattedMemoryString(): String {
    val kb = this / 1024
    val mb = kb / 1024
    val gb = mb / 1024

    return when {
        gb > 0 -> "${gb}GB"
        mb > 0 -> "${mb}MB"
        kb > 0 -> "${kb}KB"
        else -> "${this}B"
    }
}