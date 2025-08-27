package com.pine.pinedroid.net

import com.google.gson.reflect.TypeToken
import com.pine.pinedroid.utils.gson
import com.pine.pinedroid.utils.log.loge
import com.pine.pinedroid.utils.log.logv
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

val ktor by lazy {
    HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }
}

var httpRootUrl = "";

suspend inline fun <reified T> httpGet(url: String): T? {
    return try {
        val result: String = ktor.get(httpRootUrl + url).body()
        //logv(result)
        val type = object : TypeToken<T>() {}.type
        gson.fromJson<T>(result, type)
    } catch (e: Exception) {
        loge(e)
        null
    }
}

suspend inline fun <reified T> httpPostJson(
    url: String,
    body: Any
): T? {

    var result: String = ""
    return try {
        val body = gson.toJson(body)
        logv(body)
        result = ktor.post(httpRootUrl + url) {
            contentType(ContentType.Application.Json)
            setBody(body) // 直接传对象，ktor 会转成 JSON
        }.body()
        logv(result)
        val type = object : TypeToken<T>() {}.type
        gson.fromJson<T>(result, type)
    } catch (e: Exception) {
        logv(result)
        loge(e)
        null
    }
}
