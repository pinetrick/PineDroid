package com.pine.pinedroid.activity.table_selection

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pine.pinedroid.jetpack.viewmodel.HandleNavigation

@Composable
fun TableSelection(
    navController: NavController? = null,
    viewModel: QuestionViewModel = viewModel()
) {


    // 处理导航
    HandleNavigation(navController = navController, viewModel = viewModel)
}
