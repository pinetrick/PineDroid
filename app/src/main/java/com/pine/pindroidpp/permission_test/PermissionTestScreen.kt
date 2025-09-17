package com.pine.pindroidpp.permission_test


import android.Manifest
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pine.pinedroid.jetpack.ui.button.PineButton
import com.pine.pinedroid.jetpack.ui.loading.PineLoading
import com.pine.pinedroid.jetpack.viewmodel.HandleNavigation

@Composable
fun PermissionTestScreen(
    navController: NavController? = null,
    viewModel: PermissionTestScreenVM = viewModel()
) {
    val viewState by viewModel.viewState.collectAsState()

    HandleNavigation(navController = navController, viewModel = viewModel) {
        viewModel.onInit()
    }

    if (viewState.isLoading) {
        PineLoading()
    } else {
        PineButton("Permission state: " + viewState.hasPermission)
    }

}

