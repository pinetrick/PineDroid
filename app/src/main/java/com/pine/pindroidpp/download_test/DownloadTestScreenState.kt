package com.pine.pindroidpp.download_test

import com.pine.pinedroid.activity.image_pickup.OneImage
import com.pine.pinedroid.utils.shrinker_keep.Keep

@Keep
data class DownloadTestScreenState(
    var url: OneImage = OneImage.HttpImage("https://upload.intbr.com/upload/tour/admin/2025-09-12/d9e728d38c29a46b92f095e371738c11_low.jpg")

){}