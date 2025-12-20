package com.pine.pinedroid.utils
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.pine.pinedroid.utils.log.logd
import java.io.ByteArrayOutputStream
import java.io.File

object ImageExt {

    /**
     * 只进行质量压缩
     * @param inputPath 输入图片文件路径
     * @param quality 压缩质量 (0-100)
     * @return 压缩后的字节数组
     */
    fun compressImageQuality(inputPath: String, quality: Int): ByteArray {
        // 1. 加载原始图片
        val bitmap = BitmapFactory.decodeFile(inputPath)
            ?: throw IllegalArgumentException("无法加载图片: $inputPath")

        // 2. 验证 quality 参数
        val validQuality = quality.coerceIn(0, 100)

        // 3. 进行质量压缩
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, validQuality, outputStream)

        // 4. 释放 Bitmap 资源
        bitmap.recycle()

        return outputStream.toByteArray()
    }

    /**
     * 质量压缩并保存到文件
     * @param inputPath 输入图片路径
     * @param outputPath 输出图片路径
     * @param quality 压缩质量 (0-100)
     * @return 是否压缩成功
     */
    fun compressImageQualityToFile(inputPath: String, outputPath: String, quality: Int): Boolean {
        return try {
            logd("ImageZip", inputPath)
            val compressedData = compressImageQuality(inputPath, quality)

            File(outputPath).parentFile?.mkdirs()
            File(outputPath).writeBytes(compressedData)

            logd("ImageZipped", outputPath)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}