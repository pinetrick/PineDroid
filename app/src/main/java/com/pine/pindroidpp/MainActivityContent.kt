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
import com.pine.pindroidpp.empty_screen.EmptyScreen
import com.pine.pindroidpp.empty_screen.EmptyScreenVM
import com.pine.pindroidpp.location_screen.LocationScreen
import com.pine.pindroidpp.permission_test.PermissionTestScreen
import com.pine.pindroidpp.permission_test.PermissionTestScreenVM
import com.pine.pindroidpp.root.RootScreen
import com.pine.pindroidpp.root.RootScreenVM
import com.pine.pindroidpp.ui.theme.PinDroidppTheme

@Composable
fun GetRootContent(){
    PinDroidppTheme {
        // 1. 创建 NavController
        val navController = rememberNavController()

        // 2. Scaffold 包裹页面，可加入顶部栏/底部栏
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->

            // 3. NavHost 管理不同 Composable 页面
            NavHost(
                navController = navController,
                startDestination = "root",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("root") { RootScreen(navController) }
                composable("demo") { DemoScreen(navController) }
                composable("location") { LocationScreen(navController) }
                composable("empty") { EmptyScreen(navController) }
                composable("permission_test") { PermissionTestScreen(navController) }



            }
        }
    }
}