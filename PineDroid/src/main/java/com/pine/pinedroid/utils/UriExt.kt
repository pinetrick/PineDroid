package com.pine.pinedroid.utils

import androidx.exifinterface.media.ExifInterface
import android.net.Uri
import android.provider.OpenableColumns
import com.google.common.io.Files.getFileExtension
import com.pine.pinedroid.hardware.gps.PineLatLng
import com.pine.pinedroid.utils.log.logw
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

// 将 Uri 转成 File（写入临时缓存文件）
fun Uri.toFile(): File? {
    return try {
        val inputStream: InputStream? = appContext.contentResolver.openInputStream(this)
        if (inputStream == null) return null


        // 尝试获取原始文件名和扩展名
        val originalName = getOriginalFileName()
        val fileExtension = originalName?.let { getFileExtension(it) }


        // 创建临时文件，保留原始扩展名
        val tempFile = File.createTempFile(
            "upload_${System.currentTimeMillis()}_",
            if (fileExtension != null && fileExtension.isNotEmpty()) ".$fileExtension" else ".tmp",
            appContext.cacheDir
        )
        FileOutputStream(tempFile).use { output ->
            inputStream.copyTo(output)
        }
        tempFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

// 获取原始文件名
private fun Uri.getOriginalFileName(): String? {
    return try {
        val cursor = appContext.contentResolver.query(this, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (displayNameIndex != -1) {
                    it.getString(displayNameIndex)
                } else {
                    null
                }
            } else {
                null
            }
        }
    } catch (e: Exception) {
        logw("Failed to get original file name", e)
        null
    }
}

// 将 Uri 转成 File（写入临时缓存文件）
fun Uri.toLocalUrl(): String? = this.toFile()?.absolutePath

fun Uri.getPineLatLng(): PineLatLng? {
    // 尝试不同的参数名称
    val possibleLatKeys = listOf("lat", "latitude", "y", "coord_y")
    val possibleLngKeys = listOf("lng", "lon", "long", "longitude", "x", "coord_x")
    val possibleAltKeys = listOf("alt", "altitude", "z", "coord_z")
    val possibleAccKeys = listOf("acc", "accuracy", "precision")

    val lat: Double? = findFirstNonNullParameter(this, possibleLatKeys)?.toDoubleOrNull()
    val lng: Double? = findFirstNonNullParameter(this, possibleLngKeys)?.toDoubleOrNull()
    val alt = findFirstNonNullParameter(this, possibleAltKeys)?.toDoubleOrNull() ?: 0.0
    val acc = findFirstNonNullParameter(this, possibleAccKeys)?.toFloatOrNull() ?: 0f

    if (lat != null && lng != null) {
        return PineLatLng(
            lat,
            lng,
            alt,
            acc,
        )
    }

    return appContext.contentResolver.openInputStream(this)?.use { inputStream ->
        return try {
            val exif = ExifInterface(inputStream)
            val latLong = FloatArray(2)

            if (exif.getLatLong(latLong)) {
                PineLatLng(
                    latLong[0].toDouble(),
                    latLong[1].toDouble(),
                    0.0, // 高度可能需要从其他 EXIF 标签获取
                    0f   // 精度信息
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }




}

private fun findFirstNonNullParameter(uri: Uri, keys: List<String>): String? {
    return keys.firstOrNull { uri.getQueryParameter(it) != null }?.let { uri.getQueryParameter(it) }
}