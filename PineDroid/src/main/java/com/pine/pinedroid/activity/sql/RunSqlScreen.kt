package com.pine.pinedroid.activity.sql

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pine.pinedroid.activity.db_selection.DbSelectionVM
import com.pine.pinedroid.jetpack.viewmodel.HandleNavigation

@Composable
fun RunSqlScreen(
    navController: NavController? = null,
    viewModel: DbSelectionVM = viewModel()
) {
    // 处理导航
    HandleNavigation(navController = navController, viewModel = viewModel)
}
