package com.pine.pinedroid.activity.image_pickup

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Size
import androidx.core.net.toUri
import com.pine.pinedroid.R
import com.pine.pinedroid.utils.ImageExt
import com.pine.pinedroid.utils.appContext
import com.pine.pinedroid.utils.toLocalUrl

sealed class OneImage {
    data class UriImage(val uri: Uri) : OneImage() {
        val thumbnail: Bitmap?
            get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                appContext.contentResolver.loadThumbnail(uri, Size(200, 200), null)
            } else {
                null
            }
    }

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


fun String.toOneImage(): OneImage{
    return when {
        this.startsWith("http", ignoreCase = true) -> OneImage.HttpImage(this)
        this.startsWith("/") -> OneImage.LocalImage(this)
        else -> OneImage.UriImage(this.toUri())
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

// quality: 0-100，数值越小压缩率越高
fun OneImage.compress(quality: Int = 100): String? {
    val localUrl = this.toLocalUrl() ?: return null
    if (quality == 100) return localUrl

    ImageExt.compressImageQualityToFile(localUrl, localUrl, quality)
    return localUrl
}


val DEMO_ONE_IMAGE_LIST = listOf<OneImage>(
    OneImage.Resource(R.drawable.pinedroid_image_off),
    OneImage.Resource(R.drawable.pinedroid_image_loading),
    OneImage.Resource(R.drawable.pinedroid_warning),
    OneImage.Resource(R.drawable.pine_droid_icon),
)