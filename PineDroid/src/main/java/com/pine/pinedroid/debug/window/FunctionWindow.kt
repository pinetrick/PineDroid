package com.pine.pinedroid.debug.window

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatImageView
import com.pine.pinedroid.R
import com.pine.pinedroid.utils.ui.dp2Px
import com.pine.pinedroid.utils.ui.pcth
import com.pine.pinedroid.utils.ui.pctw

class FunctionWindow @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var closeButton: ImageButton
    var exitApp: Button

    init {
        // 加载 XML 布局
        inflate(context, R.layout.function_window, this)

        // 设置全屏
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )


        // 关闭按钮
        closeButton = findViewById<ImageButton>(R.id.btn_close)

        // 功能按钮示例：退出应用
        exitApp = findViewById<Button>(R.id.btn_exit_app)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // 强制大小 128dp x 400dp
        val width = 90.pctw.dp2Px()
        val height = 90.pcth.dp2Px()

        val newWidthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY)
        val newHeightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)

        super.onMeasure(newWidthSpec, newHeightSpec)
    }
}
