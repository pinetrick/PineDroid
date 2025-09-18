package com.pine.pindroidpp.download_test


import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pine.pinedroid.jetpack.ui.image.PineAsyncImage
import com.pine.pinedroid.jetpack.ui.nav.PineGeneralScreen
import com.pine.pinedroid.jetpack.ui.nav.PineTopAppBar
import com.pine.pinedroid.jetpack.viewmodel.HandleNavigation

@Composable
fun DownloadTestScreen(
    navController: NavController? = null,
    viewModel: DownloadTestScreenVM = viewModel()
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
fun Content(viewModel: DownloadTestScreenVM, viewState: DownloadTestScreenState) {
    PineAsyncImage(model = viewState.url)
}


@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 800,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun PreviewDark() {
    DownloadTestScreen()

}
