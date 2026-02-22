package com.pine.pindroidpp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pine.pindroidpp.demo_screen.DemoScreen
import com.pine.pindroidpp.dialog_demo.DialogDemoScreen
import com.pine.pindroidpp.download_test.DownloadTestScreen
import com.pine.pindroidpp.empty_screen.EmptyScreen
import com.pine.pindroidpp.location_screen.LocationScreen
import com.pine.pindroidpp.permission_test.PermissionTestScreen
import com.pine.pindroidpp.root.RootScreen
import com.pine.pindroidpp.table_demo.TableDemoScreen
import com.pine.pindroidpp.ui_showcase.UiShowcaseScreen
import com.pine.pindroidpp.ui.theme.PinDroidppTheme
import com.pine.pinedroid.language.language_switch_screen.LanguageSwitchScreen

@Composable
fun GetRootContent() {
    PinDroidppTheme {
        val navController = rememberNavController()

        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->

            NavHost(
                navController = navController,
                startDestination = "root",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("root") { RootScreen(navController) }
                composable("demo") { DemoScreen(navController) }
                composable("ui_showcase") { UiShowcaseScreen(navController) }
                composable("dialog_demo") { DialogDemoScreen(navController) }
                composable("table_demo") { TableDemoScreen(navController) }
                composable("location") { LocationScreen(navController) }
                composable("empty") { EmptyScreen(navController) }
                composable("permission_test") { PermissionTestScreen(navController) }
                composable("download_test") { DownloadTestScreen(navController) }
                composable("language_switch") { LanguageSwitchScreen(navController) }
            }
        }
    }
}
