package com.pine.pinedroid.activity.image_pickup

import com.pine.pinedroid.net.PineImageLocalCache
import com.pine.pinedroid.net.httpDownload
import com.pine.pinedroid.utils.appContext
import com.pine.pinedroid.utils.md5
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale


suspend fun OneImage.HttpImage.toLocalImage(): OneImage.LocalImage =
    withContext(Dispatchers.IO) {
        OneImage.LocalImage(PineImageLocalCache.fromUrl(url).absolutePath)
    }




