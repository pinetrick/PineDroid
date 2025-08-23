package com.pine.pinedroid.net

import com.google.gson.reflect.TypeToken
import com.pine.pinedroid.utils.gson
import com.pine.pinedroid.utils.loge
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
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
        val type = object : TypeToken<T>() {}.type
        gson.fromJson<T>(result, type)
    } catch (e: Exception) {
        loge(e)
        null
    }
}