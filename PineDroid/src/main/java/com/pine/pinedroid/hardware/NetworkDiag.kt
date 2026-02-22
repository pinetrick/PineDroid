package com.pine.pinedroid.hardware

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.pine.pinedroid.utils.appContext
import java.net.NetworkInterface

object NetworkDiag {

    /** 返回一行网络摘要，如 "WiFi · 192.168.1.5" 或 "Mobile · 10.0.2.2" 或 "No Network" */
    fun getSummary(): String {
        val type = getConnectionType()
        if (type == "No Network") return type
        val ip = getLocalIpv4() ?: "?"
        return "$type · $ip"
    }

    fun getConnectionType(): String {
        val cm = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val caps = cm.getNetworkCapabilities(cm.activeNetwork) ?: return "No Network"
        return when {
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)     -> "WiFi"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Mobile"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)      -> "VPN"
            else -> "Unknown"
        }
    }

    /** 从 NetworkInterface 枚举本机 IPv4，不需要额外权限 */
    fun getLocalIpv4(): String? {
        return try {
            NetworkInterface.getNetworkInterfaces()
                ?.asSequence()
                ?.filter { it.isUp && !it.isLoopback }
                ?.flatMap { it.inetAddresses.asSequence() }
                ?.firstOrNull { !it.isLoopbackAddress && it.address.size == 4 }
                ?.hostAddress
        } catch (_: Exception) { null }
    }
}
