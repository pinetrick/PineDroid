package com.pine.pindroidpp.root

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pine.pinedroid.jetpack.ui.button.PineButton
import com.pine.pinedroid.jetpack.ui.nav.PineGeneralScreen
import com.pine.pinedroid.jetpack.ui.nav.PineTopAppBar
import com.pine.pinedroid.jetpack.viewmodel.HandleNavigation

@Composable
fun RootScreen(
    navController: NavController? = null,
    viewModel: RootScreenVM = viewModel()
) {
    val viewState by viewModel.viewState.collectAsState()
    HandleNavigation(navController = navController, viewModel = viewModel) {
        viewModel.onInit()
    }

    PineGeneralScreen(
        title = {
            PineTopAppBar(
                title = "Home Screen"
            )
        },
        content = {
            RootScreenContent(viewModel = viewModel)
        },
    )
}

@Composable
fun RootScreenContent(
    viewModel: RootScreenVM
) {
    Column {
        Button(onClick = viewModel::onImagePickUp) {
            Text("Pick Up Image")
        }
        Button(onClick = viewModel::onTakePhoto) {
            Text("Take Photo")
        }
        Button(onClick = viewModel::onDbTest) {
            Text("Database Test")
        }
        Button(onClick = viewModel::onMessageBoxTest) {
            Text("Messagebox Test")
        }
        Button(onClick = {viewModel.navigateTo("demo")}) {
            Text("Demo Screen")
        }
        PineButton(
            text = "Record location",
            onClick =  {viewModel.navigateTo("location")}
        )

    }
}