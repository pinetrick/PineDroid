package com.pine.pindroidpp.root

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pine.pinedroid.R
import com.pine.pinedroid.activity.image_pickup.pickup.Content
import com.pine.pinedroid.activity.image_pickup.pickup.Title
import com.pine.pinedroid.jetpack.ui.nav.GeneralPineScreen
import com.pine.pinedroid.jetpack.ui.nav.PineTopAppBar
import com.pine.pinedroid.jetpack.viewmodel.HandleNavigation

@Composable
fun RootScreen(
    navController: NavController? = null,
    viewModel: RootScreenVM = viewModel()
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
    }
}