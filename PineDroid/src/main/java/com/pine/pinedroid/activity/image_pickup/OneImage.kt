package com.pine.pinedroid.activity.image_pickup

import android.net.Uri

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
}
