package com.pine.pindroidpp.root

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pine.pinedroid.activity.image_pickup.OneImage
import com.pine.pinedroid.activity.image_pickup.toLocalImage
import com.pine.pinedroid.jetpack.ui.image.PineAsyncImage
import com.pine.pinedroid.jetpack.ui.nav.PineGeneralScreen
import com.pine.pinedroid.jetpack.ui.nav.PineTopAppBar
import com.pine.pinedroid.jetpack.ui.widget.PineOptionRow
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
            RootScreenContent(viewModel = viewModel, viewState = viewState)
        },
    )
}

@Composable
fun RootScreenContent(
    viewModel: RootScreenVM,
    viewState: RootScreenState
) {
    Column {
        PineAsyncImage(model = viewState.url)
        PineOptionRow(title = "Pick Up Image", onClick = viewModel::onImagePickUp)
        PineOptionRow(title = "Take Photo", onClick = viewModel::onTakePhoto)
        PineOptionRow(title = "Database Test", onClick = viewModel::onDbTest)
        PineOptionRow(title = "Messagebox Test", onClick = viewModel::onMessageBoxTest)
        PineOptionRow(title = "Demo Screen", onClick = { viewModel.navigateTo("demo") })
        PineOptionRow(
            title = "Record location",
            onClick = { viewModel.navigateTo("location") }
        )
        PineOptionRow(
            title = "Permission Required test",
            onClick = { viewModel.navigateTo("permission_test") }
        )

    }
}