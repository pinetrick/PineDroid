package com.pine.pindroidpp.location_screen


import android.Manifest
import android.content.res.Configuration
import android.os.Build
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pine.pinedroid.jetpack.ui.nav.PineGeneralScreen
import com.pine.pinedroid.jetpack.ui.nav.PineTopAppBar
import com.pine.pinedroid.jetpack.ui.require_permission.rememberPermissionController
import com.pine.pinedroid.jetpack.viewmodel.HandleNavigation

@Composable
fun LocationScreen(
    navController: NavController? = null,
    viewModel: LocationScreenVM = viewModel()
) {
    val viewState by viewModel.viewState.collectAsState()

    HandleNavigation(navController = navController, viewModel = viewModel) {

    }
    Content(viewModel, viewState)

}

@Composable
fun Content(viewModel: LocationScreenVM, viewState: LocationScreenState) {
    // 构建正确的权限列表
    val requiredPermissions = buildList {



        add(Manifest.permission.ACCESS_FINE_LOCATION)
        add(Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    var hasPermission by remember { mutableStateOf<Boolean?>(null) }

    val permissionController = rememberPermissionController(
        permissions = requiredPermissions,
        onGranted = {
            hasPermission = true
            viewModel.locationRecordStart()
        },
        onDenied = {
            hasPermission = false
            it.requestPermissions()
        },
        onShowRationale = {
            hasPermission = null
        },
    )


    when (hasPermission) {
        true -> {

        }

        false -> {
            Button(onClick = { permissionController.requestPermissions() }) {
                Text("请求权限")
            }
        }

        null -> {
            Button(onClick = { permissionController.requestPermissions() }) {
                Text("永久被拒绝")
            }
        }
    }
}


@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 800,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun PreviewDarkLocationScreen() {
    LocationScreen()

}
