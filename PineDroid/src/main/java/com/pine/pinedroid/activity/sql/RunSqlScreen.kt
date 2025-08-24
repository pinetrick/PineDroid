package com.pine.pinedroid.activity.sql

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pine.pinedroid.activity.db_selection.DbSelectionVM
import com.pine.pinedroid.jetpack.viewmodel.HandleNavigation

@Composable
fun RunSqlScreen(
    navController: NavController? = null,
    dbName: String = "",
    tableName: String = "",
    viewModel: RunSqlScreenVM = viewModel()
) {
    // 处理导航
    HandleNavigation(navController = navController, viewModel = viewModel)

    LaunchedEffect(dbName, tableName) {
        if (dbName.isNotEmpty()) {
            viewModel.initialize(dbName, tableName)
        }
    }
}
