package com.pine.pinedroid.debug.window

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.pine.pinedroid.R
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
        val view: View

        if (convertView == null) {
            // 加载布局文件
            val inflater = LayoutInflater.from(parent?.context)
            view = inflater.inflate(R.layout.function_window_adapter, parent, false)
        } else {
            view = convertView
        }

        val icon: TextView = view.findViewById(R.id.icon)
        val text: TextView = view.findViewById(R.id.text)

        // 获取当前按钮数据
        val buttonBean = PineDebugWindow.buttons[position]

        // 设置图标和文本
        icon.text = buttonBean.icon
        text.text = buttonBean.text

        // 设置整体点击事件
        view.setOnClickListener {
            buttonBean.action.invoke()
        }

        return view
    }
}