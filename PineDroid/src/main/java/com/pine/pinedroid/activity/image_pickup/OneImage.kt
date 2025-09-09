package com.pine.pinedroid.activity.image_pickup

import android.net.Uri
import androidx.core.net.toUri
import com.pine.pinedroid.hardware.gps.PineLatLng
import com.pine.pinedroid.utils.toLocalUrl

sealed class OneImage {
    data class UriImage(val uri: Uri) : OneImage()
    data class LocalImage(val localUrl: String) : OneImage()
    data class Resource(val resourceId: Int) : OneImage()
    data class HttpImage(val url: String) : OneImage()


    override fun toString(): String {
        return when (this) {
            is UriImage -> "UriImage: $uri"
            is LocalImage -> "LocalImage: $localUrl"
            is Resource -> "Resource: $resourceId"
            is HttpImage -> "HttpImage: $url"
        }
    }

    companion object {
        fun fromString(string: String): OneImage {
            return when {
                string.startsWith("http", ignoreCase = true) -> OneImage.HttpImage(string)
                string.startsWith("/") -> OneImage.LocalImage(string)
                else -> OneImage.UriImage(string.toUri())
            }
        }
    }
}


fun OneImage.toLocalUrl(): String? {
    return when (this) {
        is OneImage.UriImage -> {
            uri.toLocalUrl()!!
        }

        is OneImage.LocalImage -> {
            this.localUrl
        }

        else -> null
    }
}
