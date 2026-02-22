package com.pine.pinedroid.screen.diagnostic

import android.app.ActivityManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Environment
import android.os.StatFs
import androidx.lifecycle.viewModelScope
import com.pine.pinedroid.jetpack.viewmodel.BaseViewModel
import com.pine.pinedroid.utils.appContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class DiagnosticScreenVM : BaseViewModel<DiagnosticScreenState>(DiagnosticScreenState::class) {

    suspend fun onInit() {
        loadDiagnostics()
    }

    fun refresh() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                loadDiagnostics()
            }
        }
    }

    private suspend fun loadDiagnostics() {
        // Memory
        val runtime = Runtime.getRuntime()
        val heapUsed = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
        val heapMax = runtime.maxMemory() / (1024 * 1024)

        val activityManager = appContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfoArray = activityManager.getProcessMemoryInfo(intArrayOf(android.os.Process.myPid()))
        val pss = memInfoArray[0].totalPss / 1024

        val ramInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(ramInfo)
        val ramAvail = ramInfo.availMem / (1024 * 1024)
        val ramTotal = ramInfo.totalMem / (1024 * 1024)

        // CPU
        val cpuCores = Runtime.getRuntime().availableProcessors()
        val cpuAbi = Build.SUPPORTED_ABIS.firstOrNull() ?: "Unknown"

        // Network
        val cm = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetwork
        val capabilities = cm.getNetworkCapabilities(activeNetwork)
        val netType = when {
            capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> "Wi-Fi"
            capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> "Cellular"
            capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true -> "Ethernet"
            else -> "None"
        }
        val netConnected = activeNetwork != null
        val netHasInternet = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true

        // Storage
        val statFs = StatFs(Environment.getDataDirectory().path)
        val storageTotal = statFs.totalBytes / (1024f * 1024 * 1024)
        val storageAvail = statFs.availableBytes / (1024f * 1024 * 1024)

        val cacheSize = getFolderSize(appContext.cacheDir) / (1024 * 1024)

        // Device
        val manufacturer = Build.MANUFACTURER.replaceFirstChar { it.uppercase() }

        setState {
            copy(
                heapUsedMb = "$heapUsed MB",
                heapMaxMb = "$heapMax MB",
                pssMb = "$pss MB",
                ramAvailMb = "$ramAvail MB",
                ramTotalMb = "$ramTotal MB",
                cpuCores = cpuCores.toString(),
                cpuAbi = cpuAbi,
                netType = netType,
                netConnected = if (netConnected) "Yes" else "No",
                netHasInternet = if (netHasInternet) "Yes" else "No",
                storageTotal = "%.1f GB".format(storageTotal),
                storageAvail = "%.1f GB".format(storageAvail),
                cacheSizeMb = "$cacheSize MB",
                deviceModel = Build.MODEL,
                manufacturer = manufacturer,
                androidVersion = "Android ${Build.VERSION.RELEASE}",
                apiLevel = "API ${Build.VERSION.SDK_INT}",
            )
        }
    }

    private fun getFolderSize(file: File): Long {
        if (!file.exists()) return 0L
        return if (file.isDirectory) {
            (file.listFiles() ?: emptyArray()).sumOf { getFolderSize(it) }
        } else {
            file.length()
        }
    }
}
