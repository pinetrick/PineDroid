package com.pine.pinedroid.net

import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import com.pine.pinedroid.activity.image_pickup.OneImage
import com.pine.pinedroid.utils.appContext
import com.pine.pinedroid.utils.md5
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
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

    suspend fun fromJson(json: String) = coroutineScope {
        withContext(Dispatchers.IO) {
            try {
                val root = if (json.trim().startsWith("[")) {
                    JSONArray(json)
                } else {
                    JSONObject(json)
                }
                scanJson(root, this)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun scanJson(obj: Any?, scope: CoroutineScope) {
        when (obj) {
            is JSONObject -> {
                for (key in obj.keys()) {
                    scanJson(obj.get(key), scope)
                }
            }
            is JSONArray -> {
                for (i in 0 until obj.length()) {
                    scanJson(obj.get(i), scope)
                }
            }
            is String -> {
                if (obj.lowercase().startsWith("https://")) {

                    scope.launch(Dispatchers.IO) {
                        semaphore.acquire()
                        try {
                            fromUrl(obj)
                        } finally {
                            semaphore.release()
                        }
                    }
                }
            }
        }
    }

    suspend fun fromUrl(url: String) : File =
        withContext(Dispatchers.IO) {
            // 获取文件扩展名
            val extension = getFileExtensionFromUrl(url, "jpg")

            // 生成 MD5 文件名
            val filename = "${md5(url)}.$extension"

            // 缓存目录
            val cacheDir = File(appContext.cacheDir, "pine_image_cache")
            if (!cacheDir.exists()) {
                cacheDir.mkdirs()
            }

            // 目标文件
            val cachedFile = File(cacheDir, filename)

            // 判断文件是否存在，不存在则下载
            if (!cachedFile.exists()) {
                httpDownload(url, cachedFile)
            }

            // 返回 LocalImage
           cachedFile
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



