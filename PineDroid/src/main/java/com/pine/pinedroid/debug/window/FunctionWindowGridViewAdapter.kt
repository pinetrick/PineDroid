package com.pine.pinedroid.debug.window

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.Button
import com.pine.pinedroid.ui.float_window.FloatingWindowHelper
import com.pine.pinedroid.utils.appContext


class FunctionWindowGridViewAdapter() : BaseAdapter() {


    override fun getCount(): Int {
        return PineDebugWindow.buttons.count()
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {


        val button: Button

        if (convertView == null) {
            // 动态创建Button
            button = Button(appContext)


            // 设置GridView布局参数
            button.setLayoutParams(
                AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
            button.setBackgroundColor(Color.TRANSPARENT)


//            // 设置Button样式
//            button.setPadding(20, 20, 20, 20)
//            button.setTextSize(14f)
//            button.setMinWidth(200)
//            button.setMinHeight(120)
        } else {
            button = convertView as Button
        }

        val text = PineDebugWindow.buttons.keys.toList()[position]
        button.text = text


        // 设置点击事件
        button.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                FunctionWindowController.closeFloatWindow()
                val text = PineDebugWindow.buttons.values.toList()[position]
                text.invoke()

            }
        })

        return button
    }
}