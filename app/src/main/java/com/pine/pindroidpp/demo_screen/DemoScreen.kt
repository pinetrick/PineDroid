package com.pine.pindroidpp.demo_screen


import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pine.pinedroid.jetpack.ui.nav.PineGeneralScreen
import com.pine.pinedroid.jetpack.ui.nav.PineTopAppBar
import com.pine.pinedroid.jetpack.viewmodel.HandleNavigation

@Composable
fun DemoScreen(
    navController: NavController? = null,
    viewModel: DemoScreenVM = viewModel()
) {
    val viewState by viewModel.viewState.collectAsState()

    HandleNavigation(navController = navController, viewModel = viewModel) {
        viewModel.onInit()
    }

    PineGeneralScreen(
        title = {
            PineTopAppBar(
                title = "Home Screen",
                onReturn =  viewModel::navigateBack
            )
        },
        content = {
            Content(viewModel, viewState)
        },
    )
}

@Composable
fun Content(viewModel: DemoScreenVM, viewState: DemoScreenState) {

}