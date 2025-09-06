package com.pine.pindroidpp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.tooling.preview.Preview
import com.pine.pindroidpp.db.TableTest
import com.pine.pindroidpp.ui.theme.PinDroidppTheme
import com.pine.pinedroid.db.model
import com.pine.pinedroid.db.table
import com.pine.pinedroid.language._appLocaleResource
import com.pine.pinedroid.utils.log.log
import com.pine.pinedroid.utils.toast
import com.pine.pinedroid.utils.ui.spw

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        TableTest.createTables()
        TableTest.testInsert()
        TableTest.testSearch()
        TableTest.loadRelative()


        // MessageBox.i().setListener { messageBoxChoose -> }.show("test" ,"te")

        enableEdgeToEdge()
        setContent {
            CompositionLocalProvider(LocalResources provides _appLocaleResource.value) {
                GetRootContent()
            }
        }
    }
}

