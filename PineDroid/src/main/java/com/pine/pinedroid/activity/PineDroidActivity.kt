package com.pine.pinedroid.activity

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pine.pinedroid.activity.db_selection.DbSelection
import com.pine.pinedroid.activity.sql.RunSqlScreen
import com.pine.pinedroid.activity.table_selection.TableSelection
import com.pine.pinedroid.db.model
import com.pine.pinedroid.db.table
import com.pine.pinedroid.ui.message_box.MessageBox
import com.pine.pinedroid.utils.log
import com.pine.pinedroid.utils.toast
import com.pine.pinedroid.utils.ui.spw


class PineDroidActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            // 1. 创建 NavController
            val navController = rememberNavController()

            // 2. Scaffold 包裹页面，可加入顶部栏/底部栏
            Scaffold(
                modifier = Modifier.fillMaxSize()
            ) { innerPadding ->

                // 3. NavHost 管理不同 Composable 页面
                NavHost(
                    navController = navController,
                    startDestination = "db",
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable("db") { DbSelection(navController) }
                    composable("table/{dbname}") { backStackEntry ->  // 注意这里改成 {dbname}
                        val dbname = backStackEntry.arguments?.getString("dbname")
                        TableSelection(navController, dbname ?: "")
                    }
                    composable("sql") { RunSqlScreen(navController) }
                }
            }

        }
    }
}
