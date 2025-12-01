package com.pine.pinedroid.system.google_map
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.pine.pinedroid.utils.currentActivity
import com.pine.pinedroid.utils.toast

object PineGoogleMap {

    fun navTo(lat: Double, lng: Double, label: String? = null) {

        // 检查谷歌地图是否安装
        val isGoogleMapsInstalled = isGoogleMapsInstalled()



        try {
            if (isGoogleMapsInstalled) {
                // 使用谷歌地图 App - 注意：谷歌地图 URI scheme 不支持 label 参数
                val uri = if (label != null) {
                    // 使用带有查询参数的格式，但注意谷歌地图原生URI可能不支持label
                    // 我们可以尝试使用 geo: URI 格式
                    Uri.parse("geo:0,0?q=$lat,$lng(${Uri.encode(label)})")
                } else {
                    Uri.parse("google.navigation:q=$lat,$lng")
                }

                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.setPackage("com.google.android.apps.maps")
                currentActivity.startActivity(intent)
            } else {
                // 如果没有安装谷歌地图，使用网页版
                val webUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$lat,$lng&travelmode=driving")
                val webIntent = Intent(Intent.ACTION_VIEW, webUri)
                currentActivity.startActivity(webIntent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // 如果启动失败，使用通用地图URI
            try {
                val fallbackUri = Uri.parse("geo:$lat,$lng?q=$lat,$lng(${label ?: "目的地"})")
                val fallbackIntent = Intent(Intent.ACTION_VIEW, fallbackUri)
                currentActivity.startActivity(fallbackIntent)
            } catch (e2: Exception) {
                toast("无法启动地图应用")
            }
        }
    }

    fun isGoogleMapsInstalled(): Boolean {
        return try {
            currentActivity.packageManager.getPackageInfo("com.google.android.apps.maps", 0)
            true
        } catch (e: Exception) {
            false
        }
    }
}