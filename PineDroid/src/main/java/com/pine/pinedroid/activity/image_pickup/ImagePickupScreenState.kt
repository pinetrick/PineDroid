package com.pine.pinedroid.activity.image_pickup

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember

data class ImagePickupScreenState(
    val selectedImages: MutableList<Uri> = mutableListOf(),
    val imageUris: MutableList<Uri> = mutableListOf()
) {}