package com.pine.pindroidpp.empty_screen


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pine.pinedroid.jetpack.ui.nav.GeneralPineScreen
import com.pine.pinedroid.jetpack.ui.nav.PineTopAppBar
import com.pine.pinedroid.jetpack.viewmodel.HandleNavigation

@Composable
fun EmptyScreen(
    navController: NavController? = null,
    viewModel: EmptyScreenVM = viewModel()
) {
    // 处理导航
    HandleNavigation(navController = navController, viewModel = viewModel)
    val viewState by viewModel.viewState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.runOnce {
            viewModel.onInit()
        }
    }

    GeneralPineScreen(
        title = {
            PineTopAppBar(
                title = "Home Screen",
                onReturn =  viewModel::navigateBack
            )
        },
        content = {

        },
    )
}