package com.pine.pindroidpp.root

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
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
                title = "PineDroid Demo"
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
        PineOptionRow(
            title = "UI Components",
            icon = "\uf5fd",
            description = "Buttons, Icons, Rating, Slider, Search, Profile...",
            onClick = { viewModel.navigateTo("ui_showcase") }
        )
        PineOptionRow(
            title = "Shopping List Demo",
            icon = "\uf290",
            description = "Grid view and list view with PineShoppingItem",
            onClick = { viewModel.navigateTo("demo") }
        )
        PineOptionRow(
            title = "Dialog Demo",
            icon = "\uf27a",
            description = "MessageBox and BottomEditText dialogs",
            onClick = { viewModel.navigateTo("dialog_demo") }
        )
        PineOptionRow(
            title = "Zoomable Table",
            icon = "\uf0ce",
            description = "Pinch-to-zoom scrollable data table",
            onClick = { viewModel.navigateTo("table_demo") }
        )
        PineOptionRow(
            title = "Image Picker",
            icon = "\uf03e",
            description = "Pick image from gallery with camera support",
            onClick = viewModel::onImagePickUp
        )
        PineOptionRow(
            title = "Take Photo",
            icon = "\uf030",
            description = "Capture photo or video",
            onClick = viewModel::onTakePhoto
        )
        PineOptionRow(
            title = "Database Test",
            icon = "\uf1c0",
            description = "ORM create/insert/query/delete operations",
            onClick = viewModel::onDbTest
        )
        PineOptionRow(
            title = "Download / Async Image",
            icon = "\uf019",
            description = "PineAsyncImage with remote URL loading",
            onClick = { viewModel.navigateTo("download_test") }
        )
        PineOptionRow(
            title = "Language Switch",
            icon = "\uf0ac",
            description = "App language localization settings",
            onClick = { viewModel.navigateTo("language_switch") }
        )
        PineOptionRow(
            title = "Location Recorder",
            icon = "\uf3c5",
            description = "GPS route recording with PineRouteRecorder",
            onClick = { viewModel.navigateTo("location") }
        )
        PineOptionRow(
            title = "Permission Manager",
            icon = "\uf023",
            description = "Request and manage runtime permissions",
            onClick = { viewModel.navigateTo("permission_test") }
        )
    }
}
