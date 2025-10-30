package com.pine.pinedroid.debug.task_manager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pine.pinedroid.activity.db_selection.DbSelection
import com.pine.pinedroid.activity.file_explorer.FileExplorer
import com.pine.pinedroid.activity.image_pickup.preview.ImagePreviewScreen
import com.pine.pinedroid.activity.sql.RunSqlScreen
import com.pine.pinedroid.activity.table_selection.TableSelection
import com.pine.pinedroid.activity.text_editor.TextEditorScreen
import com.pine.pinedroid.debug.task_manager.task_list.TaskListScreen
import com.pine.pinedroid.language._appLocaleResource

class TaskManagerActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            CompositionLocalProvider(LocalResources provides _appLocaleResource.value) {
                MainScreen()
            }
        }
    }

    @Composable
    fun MainScreen() {// 1. 创建 NavController
        val navController = rememberNavController()

        // 2. Scaffold 包裹页面，可加入顶部栏/底部栏
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->

            // 3. NavHost 管理不同 Composable 页面
            NavHost(
                navController = navController,
                startDestination = "task_list",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("task_list") { TaskListScreen(navController) }

            }
        }
    }
}