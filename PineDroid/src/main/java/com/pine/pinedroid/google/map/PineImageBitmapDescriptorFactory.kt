package com.pine.pinedroid.google.map

import android.graphics.*
import android.view.View
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.pine.pinedroid.net.findLocalFile
import com.pine.pinedroid.utils.appContext

class PineImageBitmapDescriptorFactory {
    private val reusableBitmap: Bitmap by lazy {
        Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
    }

    fun createMarkerBitmapDescriptor(
        imageUrl: String?,
        borderColor: Int = Color.WHITE,
        borderWidth: Float = 4f,
        arrowColor: Int = Color.WHITE,
        outerCircleColor: Int = Color.LTGRAY, // 添加外层圆圈颜色参数
        outerCircleWidth: Float = 2f // 添加外层圆圈宽度参数
    ): BitmapDescriptor {
        val file = imageUrl.findLocalFile()
        val imageBitmap = if (file?.exists() == true) {
            BitmapFactory.decodeFile(file.absolutePath)
        } else {
            createDefaultCircleBitmap()
        }

        reusableBitmap.eraseColor(Color.TRANSPARENT)
        val canvas = Canvas(reusableBitmap)

        if (imageBitmap != null) {
            // 绘制带边框的圆形图片
            drawCircularImageWithBorder(canvas, imageBitmap, borderColor, borderWidth)

            if (!imageBitmap.isRecycled) {
                imageBitmap.recycle()
            }
        }

        // 绘制外层浅灰色圆圈
        drawOuterCircle(canvas, outerCircleColor, outerCircleWidth)

        // 绘制箭头（带描边，顶部不描边）
        drawArrowWithStroke(canvas, 100f, 150f, 20f, arrowColor, outerCircleColor, outerCircleWidth)

        return BitmapDescriptorFactory.fromBitmap(reusableBitmap)
    }

    private fun drawOuterCircle(canvas: Canvas, circleColor: Int, circleWidth: Float) {
        val centerX = 100f
        val centerY = 80f
        val imageRadius = 60f
        val borderWidth = 4f
        val borderRadius = imageRadius + borderWidth
        val outerCircleRadius = borderRadius + circleWidth / 2

        val circlePaint = Paint().apply {
            color = circleColor
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = circleWidth
        }

        canvas.drawCircle(centerX, centerY, outerCircleRadius, circlePaint)
    }

    private fun drawCircularImageWithBorder(canvas: Canvas, imageBitmap: Bitmap, borderColor: Int, borderWidth: Float) {
        val centerX = 100f
        val centerY = 80f
        val imageRadius = 60f
        val borderRadius = imageRadius + borderWidth

        // 绘制外边框
        val borderPaint = Paint().apply {
            color = borderColor
            isAntiAlias = true
            style = Paint.Style.FILL
        }
        canvas.drawCircle(centerX, centerY, borderRadius, borderPaint)

        // 创建圆形图片
        val circularImage = getCircularBitmap(imageBitmap, (imageRadius * 2).toInt())

        // 绘制图片
        val imageLeft = centerX - imageRadius
        val imageTop = centerY - imageRadius
        canvas.drawBitmap(circularImage, imageLeft, imageTop, null)

        if (!circularImage.isRecycled) {
            circularImage.recycle()
        }
    }

    private fun createDefaultCircleBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(120, 120, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            color = Color.LTGRAY
            isAntiAlias = true
        }
        canvas.drawCircle(60f, 60f, 60f, paint)
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

    private fun drawArrowWithStroke(
        canvas: Canvas,
        centerX: Float,
        centerY: Float,
        size: Float,
        arrowColor: Int,
        strokeColor: Int,
        strokeWidth: Float
    ) {
        // 先绘制填充的箭头
        val fillPaint = Paint().apply {
            color = arrowColor
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        val arrowPath = Path().apply {
            // 绘制箭头形状
            moveTo(centerX, centerY + size / 2)
            lineTo(centerX - size, centerY - size / 2)
            lineTo(centerX + size, centerY - size / 2)
            close()
        }

        canvas.drawPath(arrowPath, fillPaint)

        // 然后绘制箭头描边（只绘制左右两边和底部）
        val strokePaint = Paint().apply {
            color = strokeColor
            style = Paint.Style.STROKE
            this.strokeWidth = strokeWidth
            isAntiAlias = true
            strokeJoin = Paint.Join.ROUND
        }

        // 创建描边路径（只包含左右两边和底部）
        val strokePath = Path().apply {
            // 左边线 - 从底部左侧到顶部左侧
            moveTo(centerX - strokeWidth / 2, centerY + size / 2 - strokeWidth / 2)
            lineTo(centerX - size + strokeWidth / 2, centerY - size / 2 + strokeWidth / 2)

            // 右边线 - 从底部右侧到顶部右侧
            moveTo(centerX + strokeWidth / 2, centerY + size / 2 - strokeWidth / 2)
            lineTo(centerX + size - strokeWidth / 2, centerY - size / 2 + strokeWidth / 2)

            // 底部线 - 从左下到右下的弧线
            moveTo(centerX - size + strokeWidth / 2, centerY + size / 2 - strokeWidth / 2)
            arcTo(
                RectF(
                    centerX - size,
                    centerY + size / 2 - strokeWidth,
                    centerX + size,
                    centerY + size / 2 + strokeWidth
                ),
                180f,
                180f
            )
        }

        // 绘制描边
        canvas.drawPath(strokePath, strokePaint)
    }

    // 保留原有的箭头绘制方法以供兼容
    private fun drawArrow(canvas: Canvas, centerX: Float, centerY: Float, size: Float, arrowColor: Int) {
        val arrowPaint = Paint().apply {
            color = arrowColor
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        val path = Path().apply {
            // 绘制更美观的箭头
            moveTo(centerX, centerY + size / 2)
            lineTo(centerX - size, centerY - size / 2)
            lineTo(centerX + size, centerY - size / 2)
            close()
        }

        canvas.drawPath(path, arrowPaint)
    }

    fun cleanup() {
        if (!reusableBitmap.isRecycled) {
            reusableBitmap.recycle()
        }
    }
}