package com.pine.pinedroid.net

import android.webkit.MimeTypeMap
import com.pine.pinedroid.net.PineImageLocalCache.getFileExtensionFromUrl
import com.pine.pinedroid.utils.appContext
import com.pine.pinedroid.utils.log.logd
import com.pine.pinedroid.utils.log.logi
import com.pine.pinedroid.utils.md5
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.Locale

object PineImageLocalCache {
    private val semaphore = Semaphore(10) // 限制同时最多10个下载

    suspend fun fromJson(json: String) = coroutineScope { //怎么等待所有下载都结束后返回
        withContext(Dispatchers.IO) {
            try {
                val root = if (json.trim().startsWith("[")) {
                    JSONArray(json)
                } else {
                    JSONObject(json)
                }
                scanJson(root)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun scanJson(obj: Any?): Unit = coroutineScope {
        when (obj) {
            is JSONObject -> {
                for (key in obj.keys()) {
                    scanJson(obj.get(key))
                }
            }

            is JSONArray -> {
                for (i in 0 until obj.length()) {
                    scanJson(obj.get(i))
                }
            }

            is String -> {
                if (obj.lowercase().startsWith("https://")) {

                    launch(Dispatchers.IO) {
                        semaphore.acquire()
                        try {
                            obj.httpDownloadToLocalFile()
                        } finally {
                            semaphore.release()
                        }
                    }
                }
            }
        }
    }


    fun getFileExtensionFromUrl(url: String, defaultExtension: String = "jpg"): String {
        return try {
            // 去掉 query 和 fragment
            val cleanUrl = url.substringBefore('?').substringBefore('#')
            val ext = MimeTypeMap.getFileExtensionFromUrl(cleanUrl)
            if (!ext.isNullOrBlank()) {
                ext.lowercase(Locale.ROOT)
            } else {
                defaultExtension
            }
        } catch (e: Exception) {
            defaultExtension
        }
    }


}


fun String?.findLocalFile(): File? = this?.let {
    // 获取文件扩展名
    val extension = getFileExtensionFromUrl(this, "jpg")

    // 生成 MD5 文件名
    val filename = "${md5(this)}.$extension"

    // 缓存目录
    val cacheDir = File(appContext.cacheDir, "pine_image_cache")
    if (!cacheDir.exists()) {
        cacheDir.mkdirs()
    }

    // 目标文件
    val cachedFile = File(cacheDir, filename)

    // 判断文件是否存在，不存在则下载
    if (!cachedFile.exists()) {
        return null
    }
    // 返回 LocalImage
    return cachedFile
}

suspend fun String.httpDownloadToLocalFile(): File {
    val httpsUrl = this
    return withContext(Dispatchers.IO) {

        // 获取文件扩展名
        val extension = getFileExtensionFromUrl(httpsUrl, "jpg")

        // 生成 MD5 文件名
        val filename = "${md5(httpsUrl)}.$extension"

        // 缓存目录
        val cacheDir = File(appContext.cacheDir, "pine_image_cache")
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }

        // 目标文件
        val cachedFile = File(cacheDir, filename)

        // 判断文件是否存在，不存在则下载
        if (!cachedFile.exists()) {
            logi("Cache Image: $httpsUrl")
            httpDownload(httpsUrl, cachedFile)
        }
        else {
            //logd("Cache Hit: $httpsUrl")
        }

        // 返回 LocalImage
        cachedFile
    }
}