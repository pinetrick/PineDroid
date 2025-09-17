package com.pine.pinedroid.jetpack.ui.require_permission.enable_permission_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pine.pinedroid.R
import com.pine.pinedroid.jetpack.ui.widget.PineOptionRow
import com.pine.pinedroid.jetpack.viewmodel.HandleNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnablePermissionScreen(
    navController: NavController? = null,
    viewModel: EnablePermissionScreenVM = viewModel()
) {
    val viewState by viewModel.viewState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }


    HandleNavigation(navController = navController, viewModel = viewModel) {
        viewModel.onInit()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(text = "权限管理") }
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Content(
                viewModel = viewModel,
                viewState = viewState,
            )
        }
    }
}

@Composable
fun Content(
    viewModel: EnablePermissionScreenVM,
    viewState: EnablePermissionScreenState,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header section
        HeaderSection()

        Spacer(modifier = Modifier.height(24.dp))

        viewState.isCoarseLocation?.let { enabled ->
            PermissionPineOptionRow(
                icon = "\uf21d",
                title = "粗略位置",
                description = "应用获取您的粗略位置以为您提供附近导览",
                hasPermission = enabled,
                onClick = {}
            )
        }

        viewState.isLocation?.let { enabled ->
            PermissionPineOptionRow(
                icon = "\uf601",
                title = "精确位置",
                description = "获取您的精确位置以为您提供导航服务",
                hasPermission = enabled,
                onClick = {}
            )
        }

        viewState.accessCamera?.let { enabled ->
            PermissionPineOptionRow(
                icon = "\uf030",
                title = "拍照",
                description = "上传照片",
                hasPermission = enabled,
                onClick = {}
            )
        }

        viewState.accessGallery?.let { enabled ->
            PermissionPineOptionRow(
                icon = "\uf03e",
                title = "图库",
                description = "上传照片",
                hasPermission = enabled,
                onClick = {}
            )
        }
    }
}

@Composable
fun HeaderSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "需要权限",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "为了应用正常运行，请启用以下所需权限",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

data class PermissionItem(
    val name: String,
    val granted: Boolean?,
    val description: String
)


@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 800
)
@Composable
fun PreviewLight() {
    MaterialTheme {
        Surface {
            EnablePermissionScreen()
        }
    }
}