package com.pine.pinedroid.debug.window

import android.annotation.SuppressLint
import android.view.View
import com.pine.pinedroid.ui.float_window.FloatingWindowHelper
import com.pine.pinedroid.utils.appContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object FunctionWindowController {
    @SuppressLint("StaticFieldLeak")
    var functionWindow: FunctionWindow? = null

    fun onShowFunctionWindowClick(view: View) {
        CoroutineScope(Dispatchers.Main).launch {
            if (functionWindow == null) {
                functionWindow = FunctionWindow(appContext)
                functionWindow!!.exitApp.setOnClickListener {
                    android.os.Process.killProcess(android.os.Process.myPid())
                }
                functionWindow!!.closeButton.setOnClickListener {
                    FloatingWindowHelper.closeFloatingWindow(functionWindow!!)
                }
            }
            FloatingWindowHelper.showFloatingWindow(functionWindow!!)
        }
    }


}