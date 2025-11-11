package com.pine.pinedroid.net

import com.google.gson.reflect.TypeToken
import com.pine.pinedroid.utils.gson
import com.pine.pinedroid.utils.log.loge
import com.pine.pinedroid.utils.log.logv
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.copyAndClose
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File

val ktor by lazy {
    HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 60_000
            connectTimeoutMillis = 30_000
            socketTimeoutMillis = 60_000
        }
    }
}

var httpRootUrl = "";
var uploadRootUrl = "";

suspend fun httpDownload(imageUrl: String, outputFile: File): Boolean {
    try {
        val response: HttpResponse = ktor.get(imageUrl)

        // 创建临时文件，下载完成后再重命名，避免下载中断导致文件损坏
        val tempFile = File(outputFile.parent, "${outputFile.name}.tmp")

        response.bodyAsChannel().copyAndClose(tempFile.writeChannel())

        // 下载完成后重命名为目标文件
        tempFile.renameTo(outputFile)

        return true
    } catch (e: Exception) {
        loge("Fail to download: $imageUrl")
        loge(e)
        return false
    }
}
object BackgroundScope : CoroutineScope by CoroutineScope(Dispatchers.IO + SupervisorJob())

suspend inline fun <reified T> httpGet(url: String, cacheImages: Boolean = false): T? = withContext(Dispatchers.IO) {
    try {
        val result: String = ktor.get(httpRootUrl + url).body()
        logv(result)
        if (cacheImages) {
            BackgroundScope.launch {
                PineImageLocalCache.fromJson(result)
            }
        }
        if (T::class == String::class) {
            return@withContext result as T?
        }

        val type = object : TypeToken<T>() {}.type
        gson.fromJson<T>(result, type)
    } catch (e: Exception) {
        loge(e)
        null
    }
}

suspend inline fun <reified T> httpPostJson(
    url: String,
    body: Any,
    cacheImages: Boolean = false
): T? = withContext(Dispatchers.IO) {

    var result: String = ""
    try {
        val body = body as? String ?: gson.toJson(body)
        logv(body)
        result = ktor.post(httpRootUrl + url) {
            contentType(ContentType.Application.Json)
            setBody(body) // 直接传对象，ktor 会转成 JSON
        }.body()
        logv(result)

        if (cacheImages) {
            BackgroundScope.launch {
                PineImageLocalCache.fromJson(result)
            }
        }

        if (T::class == String::class) {
            return@withContext result as T?
        }

        val type = object : TypeToken<T>() {}.type
        gson.fromJson<T>(result, type)
    } catch (e: Exception) {
        logv(result)
        loge(e)
        null
    }
}


suspend inline fun <reified T> httpUploadFile(
    url: String,
    params: Map<String, Any> = emptyMap() // form-data 字段，可以是 File 或 String
): T? = withContext(Dispatchers.IO) {
    var result = ""
    try {
        result = ktor.submitFormWithBinaryData(
            url = uploadRootUrl + url,
            formData = formData {
                for ((key, value) in params) {
                    when (value) {
                        is File -> append(key, value.readBytes(), Headers.build {
                            append(
                                HttpHeaders.ContentDisposition,
                                "form-data; name=\"$key\"; filename=\"${value.name}\""
                            )
                            append(
                                HttpHeaders.ContentType,
                                ContentType.Application.OctetStream.toString()
                            )
                        })

                        else -> append(key, value.toString())
                    }
                }
            }
        ).bodyAsText()

        logv(result)
        val type = object : TypeToken<T>() {}.type
        gson.fromJson<T>(result, type)
    } catch (e: Exception) {
        logv(result)
        loge(e)
        null
    }
}
