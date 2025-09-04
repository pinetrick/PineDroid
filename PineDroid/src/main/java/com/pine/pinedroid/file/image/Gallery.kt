package com.pine.pinedroid.file.image

import android.net.Uri
import android.provider.MediaStore
import com.pine.pinedroid.utils.appContext

object Gallery {
    fun getGalleryImages(): List<Uri> {
        val imageUris = mutableListOf<Uri>()
        val contentResolver = appContext.contentResolver

        val projection = arrayOf(MediaStore.Images.Media._ID)
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val uri: Uri = Uri.withAppendedPath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id.toString()
                )
                imageUris.add(uri)
            }
        }

        return imageUris
    }
}