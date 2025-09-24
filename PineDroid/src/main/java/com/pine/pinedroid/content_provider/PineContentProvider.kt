package com.pine.pinedroid.content_provider

import android.app.Activity
import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import com.pine.pinedroid.PineConfig
import com.pine.pinedroid.debug.icon.SmallFloatIcon
import com.pine.pinedroid.debug.window.FunctionWindowController
import com.pine.pinedroid.debug.window.PineDebugWindow
import com.pine.pinedroid.language.LanguageManager
import com.pine.pinedroid.ui.float_window.FloatingWindowHelper
import com.pine.pinedroid.utils.PineApp.isAppDebug
import com.pine.pinedroid.utils.PineApp.printSignatures
import com.pine.pinedroid.utils.activityContext
import com.pine.pinedroid.utils.appContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PineContentProvider : ContentProvider() {


    override fun onCreate(): Boolean {
        context?.let { context ->
            activityContext = context
            appContext = context.applicationContext
            LanguageManager.applySavedLanguage()
            registerActivityLifecycleCallbacks()

            if (isAppDebug()) {
                printSignatures()
            }
            if (PineDebugWindow.getIsDebugWindowAlwaysOn()) {
                CoroutineScope(Dispatchers.Main).launch {
                    val view = SmallFloatIcon(appContext)
                    view.setOnClickListener(FunctionWindowController::onShowFunctionWindowClick)
                    FloatingWindowHelper.showFloatingWindow(view, true)
                }
            }

        }
        return true
    }

    fun registerActivityLifecycleCallbacks(){
        val app = appContext.applicationContext as Application
        app.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityResumed(activity: Activity) {
                activityContext = activity
            }

            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                activityContext = activity
            }
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }


    override fun query(
        uri: Uri, projection: Array<out String>?, selection: String?,
        selectionArgs: Array<out String>?, sortOrder: String?
    ): Cursor? = null

    override fun getType(uri: Uri): String? = null
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<out String>?
    ): Int = 0
}