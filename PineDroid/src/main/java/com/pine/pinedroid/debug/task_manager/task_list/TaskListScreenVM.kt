package com.pine.pinedroid.debug.task_manager.task_list

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.pine.pinedroid.jetpack.viewmodel.BaseViewModel
import com.pine.pinedroid.utils.adbShell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

class TaskListScreenVM : BaseViewModel<TaskListScreenState>(TaskListScreenState::class) {

    suspend fun onInit() {
        loadMemoryInfo()
    }

    fun onRefresh() = viewModelScope.launch {
        setState { copy(isLoading = true) }
        loadMemoryInfo()
    }

    private suspend fun loadMemoryInfo() {
        runCatching {
            val result = withContext(Dispatchers.IO) {
                adbShell("dumpsys meminfo")
            }
            parseMeminfoOutput(result!!)
            setState { copy(isLoading = false) }
        }
    }


    private fun parseMeminfoOutput(output: String) {
        val apps = ArrayList<AppInfos>()
        val memoryInfo = MemoryInfo()

        val lines = output.split("\n")
        var inProcessSection = false
        var inMemorySummary = false

        for (line in lines) {
            when {
                // 解析进程信息
                line.contains("Total PSS by process:") -> {
                    inProcessSection = true
                    continue
                }
                line.contains("Total PSS by OOM adjustment:") -> {
                    inProcessSection = false
                    continue
                }
                inProcessSection && line.contains("K:") -> {
                    parseProcessLine(line, apps)
                }
                // 解析内存汇总信息
                line.contains("Total RAM:") -> {
                    inMemorySummary = true
                    parseMemoryInfo(line, memoryInfo)
                }
                line.contains("Free RAM:") && inMemorySummary -> {
                    parseMemoryInfo(line, memoryInfo)
                }
                line.contains("Used RAM:") && inMemorySummary -> {
                    parseMemoryInfo(line, memoryInfo)
                }
                line.contains("Lost RAM:") && inMemorySummary -> {
                    parseMemoryInfo(line, memoryInfo)
                }
                line.contains("ZRAM:") && inMemorySummary -> {
                    parseMemoryInfo(line, memoryInfo)
                }
            }
        }

        setState {
            copy(
                apps = apps,
                memoryInfo = memoryInfo,
                isLoading = false
            )
        }
    }

    private fun parseProcessLine(line: String, apps: ArrayList<AppInfos>) {
        try {
            val parts = line.trim().split("K:")
            if (parts.size == 2) {
                val pss = parts[0].trim().replace(",", "") + "K"
                val rest = parts[1].trim()

                // 提取进程名和PID
                val pidMatch = Regex("\\(pid (\\d+)").find(rest)
                val pid = pidMatch?.groupValues?.get(1) ?: ""

                val name = rest.substringBefore("(pid").trim()
                val category = if (rest.contains("/ activities")) "Activity" else "Service"

                apps.add(AppInfos(name = name, pss = pss, pid = pid, category = category))
            }
        } catch (e: Exception) {
            // 忽略解析错误
        }
    }

    private fun parseMemoryInfo(line: String, memoryInfo: MemoryInfo) {
        when {
            line.contains("Total RAM:") -> {
                memoryInfo.totalRAM = line.substringAfter("Total RAM:").trim()
            }
            line.contains("Free RAM:") -> {
                memoryInfo.freeRAM = line.substringAfter("Free RAM:").trim()
            }
            line.contains("Used RAM:") -> {
                memoryInfo.usedRAM = line.substringAfter("Used RAM:").trim()
            }
            line.contains("Lost RAM:") -> {
                memoryInfo.lostRAM = line.substringAfter("Lost RAM:").trim()
            }
            line.contains("ZRAM:") -> {
                memoryInfo.zramInfo = line.substringAfter("ZRAM:").trim()
            }
        }
    }
}