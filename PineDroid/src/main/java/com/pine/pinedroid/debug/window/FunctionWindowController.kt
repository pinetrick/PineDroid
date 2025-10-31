package com.pine.pinedroid.debug.window

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.View
import androidx.core.net.toUri
import com.pine.pinedroid.activity.PineDroidActivity
import com.pine.pinedroid.debug.task_manager.TaskManagerActivity
import com.pine.pinedroid.file.DataCleanManager
import com.pine.pinedroid.ui.float_window.FloatingWindowHelper
import com.pine.pinedroid.ui.message_box.MessageBox
import com.pine.pinedroid.utils.Memory.getAppMemoryUsage
import com.pine.pinedroid.utils.activityContext
import com.pine.pinedroid.utils.appContext
import com.pine.pinedroid.utils.currentActivity
import com.pine.pinedroid.utils.shortName
import com.pine.pinedroid.utils.file.kbToDisplayFileSize
import com.pine.pinedroid.utils.intent
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

                    exitApp.setOnClickListener {
                        android.os.Process.killProcess(android.os.Process.myPid())
                    }
                    closeButton.setOnClickListener(::closeFloatWindow)
                    uninstallButton.setOnClickListener (::onUninstallBtnClick)
                    dataBaseButton.setOnClickListener {
                        intent(PineDroidActivity::class)
                        closeFloatWindow()
                    }

                   // PineDebugWindow.addButton("Files", "\uf15b", ::openDbEditor)
                    PineDebugWindow.addButton("Task Manager", "\uf0ae") {
                        intent(TaskManagerActivity::class)
                        closeFloatWindow()
                    }
                }
            }
            functionWindow!!.mainMessage.text = appContext.packageName + ": " + getAppMemoryUsage().kbToDisplayFileSize()
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
                val packageUri = "package:${appContext.packageName}".toUri()

                var intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = packageUri
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                currentActivity.startActivity(intent)

//                intent = Intent(Intent.ACTION_DELETE, packageUri).apply {
//                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                }
//                currentActivity.startActivity(intent)


            }
        }.show("How Can I Help You！", "Clean Data", "Uninstall", "Cancel")
    }
}