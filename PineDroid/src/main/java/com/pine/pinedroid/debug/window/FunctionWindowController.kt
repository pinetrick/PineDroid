package com.pine.pinedroid.debug.window

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import androidx.core.net.toUri
import com.pine.pinedroid.file.DataCleanManager
import com.pine.pinedroid.ui.float_window.FloatingWindowHelper
import com.pine.pinedroid.ui.message_box.MessageBox
import com.pine.pinedroid.utils.Memory.getAppMemoryUsage
import com.pine.pinedroid.utils.activityContext
import com.pine.pinedroid.utils.appContext
import com.pine.pinedroid.utils.currentActivity
import com.pine.pinedroid.utils.shortName
import com.pine.pinedroid.utils.file.kbToDisplayFileSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

object FunctionWindowController {
    @SuppressLint("StaticFieldLeak")
    var functionWindow: FunctionWindow? = null

    private var functionButtons: MutableList<Pair<String, () -> Unit>> = mutableListOf()

    fun addButton(key: String, function: () -> Unit) {
        functionButtons.add(Pair(key, function))
    }

    @SuppressLint("SetTextI18n")
    fun onShowFunctionWindowClick(view: View) {
        CoroutineScope(Dispatchers.Main).launch {
            if (functionWindow == null) {
                functionWindow = FunctionWindow(appContext).apply {

                    mainMessage.text = currentActivity().shortName + ": " + getAppMemoryUsage().kbToDisplayFileSize()
                    exitApp.setOnClickListener {
                        android.os.Process.killProcess(android.os.Process.myPid())
                    }
                    closeButton.setOnClickListener(::closeFloatWindow)
                    uninstallButton.setOnClickListener (::onUninstallBtnClick)
                }


            }
            FloatingWindowHelper.showFloatingWindow(functionWindow!!)
        }
    }

    fun closeFloatWindow(view: View? = null){
        FloatingWindowHelper.closeFloatingWindow(functionWindow!!)
    }
    fun onUninstallBtnClick(view: View){
        closeFloatWindow()
        MessageBox.i().setListener { id ->

            if (id == 1) {
                DataCleanManager.cleanInternalCache()
                DataCleanManager.cleanDatabases()
                DataCleanManager.cleanSharedPreference()
                DataCleanManager.cleanFiles()
                DataCleanManager.cleanExternalCache()

                exitProcess(0)
            } else if (id == 2) {
                val intent = Intent()
                intent.setAction(Intent.ACTION_DELETE)
                intent.setData(
                    ("package:" + appContext.packageName).toUri()
                )
                activityContext.startActivity(intent)
            }
        }.show("How Can I Help You！", "Clean Data", "Uninstall", "Cancel")
    }
}