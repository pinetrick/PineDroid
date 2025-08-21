package com.pine.pinedroid.debug.window


import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class SmallFloatIcon @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE        // 字体颜色
        textSize = 48f             // 字体大小
    }

    private val bgPaint = Paint().apply {
        color = 0x55FF0000         // 背景色（半透明红色）
    }



    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = 120      // 小尺寸宽度
        val desiredHeight = 120     // 小尺寸高度

        val width = resolveSize(desiredWidth, widthMeasureSpec)
        val height = resolveSize(desiredHeight, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 画背景矩形
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)
//
//        // 绘制文字（简单居中）
//        val textWidth = paint.measureText(text)
//        val textX = (width - textWidth) / 2
//        val textY = (height / 2 - (paint.descent() + paint.ascent()) / 2)
//        canvas.drawText(text, textX, textY, paint)
    }
}
