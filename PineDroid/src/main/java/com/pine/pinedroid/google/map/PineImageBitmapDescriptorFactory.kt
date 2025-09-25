package com.pine.pinedroid.google.map

import android.graphics.*
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.pine.pinedroid.R
import com.pine.pinedroid.net.findLocalFile
import com.pine.pinedroid.utils.appContext

class PineImageBitmapDescriptorFactory {
    private val reusableBitmap: Bitmap by lazy {
        Bitmap.createBitmap(454, 512, Bitmap.Config.ARGB_8888)
    }

    fun createMarkerBitmapDescriptor(
        imageUrl: String?
    ): BitmapDescriptor {
        // 加载背景图片
        val backgroundBitmap = BitmapFactory.decodeResource(appContext.resources, R.drawable.pinedroid_map_mark)
        val scaledBackground = Bitmap.createScaledBitmap(backgroundBitmap, 182, 205, true)

        reusableBitmap.eraseColor(Color.TRANSPARENT)
        val canvas = Canvas(reusableBitmap)

        // 绘制背景
        canvas.drawBitmap(scaledBackground, 0f, 0f, null)

        // 加载并处理用户图片
        val file = imageUrl.findLocalFile()
        val imageBitmap = if (file?.exists() == true) {
            BitmapFactory.decodeFile(file.absolutePath)
        } else {
            createDefaultCircleBitmap()
        }

        if (imageBitmap != null) {
            // 绘制圆形图片到中间位置（您可以根据需要调整位置）
            drawCircularImage(canvas, imageBitmap)

            if (!imageBitmap.isRecycled) {
                imageBitmap.recycle()
            }
        }

        // 回收背景位图
        if (!scaledBackground.isRecycled && scaledBackground != backgroundBitmap) {
            scaledBackground.recycle()
        }
        if (!backgroundBitmap.isRecycled) {
            backgroundBitmap.recycle()
        }

        return BitmapDescriptorFactory.fromBitmap(reusableBitmap)
    }

    private fun drawCircularImage(canvas: Canvas, imageBitmap: Bitmap) {
        // 图片在背景中的位置和大小 - 您可以根据需要调整这些值
        val centerX = 91f // 454 / 2
        val centerY = 90f // 根据背景图片调整这个Y坐标
        val imageDiameter = 170f // 圆形图片的直径

        // 创建圆形图片
        val circularImage = getCircularBitmap(imageBitmap, imageDiameter.toInt())

        // 绘制图片（居中）
        val imageLeft = centerX - imageDiameter / 2
        val imageTop = centerY - imageDiameter / 2
        canvas.drawBitmap(circularImage, imageLeft, imageTop, null)

        if (!circularImage.isRecycled) {
            circularImage.recycle()
        }
    }

    private fun createDefaultCircleBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            color = Color.LTGRAY
            isAntiAlias = true
        }
        canvas.drawCircle(100f, 100f, 100f, paint)
        return bitmap
    }

    private fun getCircularBitmap(bitmap: Bitmap, diameter: Int): Bitmap {
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, diameter, diameter, false)
        val output = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint().apply {
            isAntiAlias = true
        }

        // 使用 PorterDuff 模式创建圆形图片
        val rect = Rect(0, 0, diameter, diameter)
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawCircle(diameter / 2f, diameter / 2f, diameter / 2f, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(scaledBitmap, rect, rect, paint)

        if (scaledBitmap != bitmap && !scaledBitmap.isRecycled) {
            scaledBitmap.recycle()
        }

        return output
    }

    fun cleanup() {
        if (!reusableBitmap.isRecycled) {
            reusableBitmap.recycle()
        }
    }
}