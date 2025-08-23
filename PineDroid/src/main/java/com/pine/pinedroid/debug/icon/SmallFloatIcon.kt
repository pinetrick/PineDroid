package com.pine.pinedroid.debug.icon

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.pine.pinedroid.R
import com.pine.pinedroid.utils.ui.dp2Px
import com.pine.pinedroid.utils.ui.pct
import com.pine.pinedroid.utils.ui.pctw

class SmallFloatIcon @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    init {
        // 设置图片资源
        setImageResource(R.drawable.pine_droid_icon)
        scaleType = ScaleType.FIT_CENTER
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = 12.pct.dp2Px()
        val desiredHeight = 12.pct.dp2Px()

        val width = resolveSize(desiredWidth, widthMeasureSpec)
        val height = resolveSize(desiredHeight, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }
}
