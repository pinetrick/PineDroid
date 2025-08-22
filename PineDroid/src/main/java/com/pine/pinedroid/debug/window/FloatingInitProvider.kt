package com.pine.pinedroid.debug.window


import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.pine.pinedroid.PineConfig
import com.pine.pinedroid.ui.float_window.FloatingWindowHelper
import com.pine.pinedroid.utils.activityContext
import com.pine.pinedroid.utils.appContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FloatingInitProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        context?.let { context ->
            activityContext = context
            appContext = context.applicationContext
            if (PineConfig.getIsDebugWindowAlwaysOn()) {
                CoroutineScope(Dispatchers.Main).launch {
                    val view = SmallFloatIcon(appContext)
                    FloatingWindowHelper.showFloatingWindow(view, true)
                }
            }

        }
        return true
    }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?,
                       selectionArgs: Array<out String>?, sortOrder: String?): Cursor? = null
    override fun getType(uri: Uri): String? = null
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
    override fun update(uri: Uri, values: ContentValues?, selection: String?,
                        selectionArgs: Array<out String>?): Int = 0
}
