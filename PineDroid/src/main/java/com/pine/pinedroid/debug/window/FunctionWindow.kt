package com.pine.pinedroid.debug.window

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
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

    var mainMessage: TextView
    var exitApp: Button

    var closeButton: Button
    var dataBaseButton: Button
    var uninstallButton: Button


    init {
        // 加载 XML 布局
        inflate(context, R.layout.function_window, this)

        // 设置全屏
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )

        mainMessage = findViewById<TextView>(R.id.mainMassage)
        exitApp = findViewById<Button>(R.id.debug_runtime_kill_btn)
        closeButton = findViewById<Button>(R.id.debug_runtime_ret_btn)
        dataBaseButton = findViewById<Button>(R.id.debug_runtime_database_btn)
        uninstallButton = findViewById<Button>(R.id.debug_runtime_uninstall_btn)

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
