@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.pine.pinedroid.screen.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pine.pinedroid.R
import com.pine.pinedroid.jetpack.ui.nav.PineGeneralScreen
import com.pine.pinedroid.jetpack.ui.nav.PineTopAppBar
import com.pine.pinedroid.jetpack.ui.widget.PineOptionRow
import com.pine.pinedroid.jetpack.viewmodel.HandleNavigation

@Composable
fun AboutScreen(
    navController: NavController? = null,
    viewModel: AboutScreenVM = viewModel()
) {
    val viewState by viewModel.viewState.collectAsState()

    HandleNavigation(navController = navController, viewModel = viewModel) {
        viewModel.onInit()
    }

    PineGeneralScreen(
        title = {
            PineTopAppBar(
                title = stringResource(R.string.pine_about_screen_title),
                onReturn = viewModel::navigateBack
            )
        },
        content = {
            Content(viewModel, viewState)
        },
    )
}


@Composable
fun Content(viewModel: AboutScreenVM, viewState: AboutScreenState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 应用图标和标题

        Image(
            painter = painterResource(id = AboutScreenVM.logo),
            contentDescription = "App Logo",
            modifier = Modifier.size(72.dp)
        )
        Text(
            text = viewState.appName,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = viewState.versionName,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // 信息卡片
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PineOptionRow(
                    title = stringResource(R.string.pine_version_number),
                    description = viewState.versionName,
                    icon = "\ue13a"
                )
                PineOptionRow(
                    title = stringResource(R.string.pine_version_code),
                    description = viewState.versionCode.toString(),
                    icon = "\uf120"
                )
                PineOptionRow(
                    title = stringResource(R.string.pine_build_date),
                    description = viewState.buildDate,
                    icon = "\uf073"
                )
                PineOptionRow(
                    title = stringResource(R.string.pine_minimum_support),
                    description = viewState.minSdkVersion,
                    icon = "\uf544"
                )


            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 底部版权信息
        Text(
            text = AboutScreenVM.copyRight,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
