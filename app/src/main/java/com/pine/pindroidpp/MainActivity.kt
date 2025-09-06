package com.pine.pindroidpp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalResources
import androidx.core.view.WindowCompat
import com.pine.pindroidpp.ui.theme.PinDroidppTheme
import com.pine.pinedroid.language._appLocaleResource

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //全面屏手势
        WindowCompat.setDecorFitsSystemWindows(window, false)

        enableEdgeToEdge()
        setContent {
            CompositionLocalProvider(LocalResources provides _appLocaleResource.value) {
                PinDroidppTheme {
                    GetRootContent()
                }

            }
        }
    }
}

