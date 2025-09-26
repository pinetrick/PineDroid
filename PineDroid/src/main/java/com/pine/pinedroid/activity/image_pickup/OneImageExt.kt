package com.pine.pinedroid.activity.image_pickup

import com.pine.pinedroid.net.httpDownloadToLocalFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


suspend fun OneImage.HttpImage.toLocalImage(): OneImage.LocalImage =
    withContext(Dispatchers.IO) {
        OneImage.LocalImage(url.httpDownloadToLocalFile().absolutePath)
    }




