package com.pine.pinedroid.screen.permission.location

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalResources
import com.pine.pinedroid.language._appLocaleResource
import com.pine.pinedroid.screen.permission.PineOnePermission
import com.pine.pinedroid.screen.permission.PinePermissionUtils

class PinePermissionActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {


            CompositionLocalProvider(LocalResources provides _appLocaleResource.value) {
                EnableSpecifiedPermissionScreen()
            }


        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 确保在Activity销毁时清理等待状态
        if (!isFinishing) {
            PinePermissionUtils.onPermission(false)
        }
    }

    companion object {
        lateinit var state: PineOnePermission
    }
}