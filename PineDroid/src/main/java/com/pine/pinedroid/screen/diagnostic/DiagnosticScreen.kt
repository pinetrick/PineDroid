@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.pine.pinedroid.screen.diagnostic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pine.pinedroid.R
import com.pine.pinedroid.jetpack.ui.nav.PineGeneralScreen
import com.pine.pinedroid.jetpack.ui.nav.PineTopAppBar
import com.pine.pinedroid.jetpack.ui.widget.PineOptionRow
import com.pine.pinedroid.jetpack.viewmodel.HandleNavigation

@Composable
fun DiagnosticScreen(
    navController: NavController? = null,
    viewModel: DiagnosticScreenVM = viewModel()
) {
    val viewState by viewModel.viewState.collectAsState()

    HandleNavigation(navController = navController, viewModel = viewModel) {
        viewModel.onInit()
    }

    PineGeneralScreen(
        title = {
            PineTopAppBar(
                title = stringResource(R.string.pine_diag_screen_title),
                onReturn = viewModel::navigateBack,
                actionIcon = "\uf021",
                onAction = viewModel::refresh,
            )
        },
        content = {
            DiagnosticContent(viewState)
        }
    )
}

@Composable
private fun DiagnosticContent(viewState: DiagnosticScreenState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Memory
        DiagSection(stringResource(R.string.pine_diag_section_memory)) {
            PineOptionRow(stringResource(R.string.pine_diag_heap_used), "\uf538", viewState.heapUsedMb)
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            PineOptionRow(stringResource(R.string.pine_diag_heap_max), "\uf538", viewState.heapMaxMb)
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            PineOptionRow(stringResource(R.string.pine_diag_pss_memory), "\uf538", viewState.pssMb)
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            PineOptionRow(stringResource(R.string.pine_diag_ram_avail), "\uf538", viewState.ramAvailMb)
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            PineOptionRow(stringResource(R.string.pine_diag_ram_total), "\uf538", viewState.ramTotalMb)
        }

        // CPU
        DiagSection(stringResource(R.string.pine_diag_section_cpu)) {
            PineOptionRow(stringResource(R.string.pine_diag_cpu_cores), "\uf2db", viewState.cpuCores)
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            PineOptionRow(stringResource(R.string.pine_diag_cpu_abi), "\uf2db", viewState.cpuAbi)
        }

        // Network
        DiagSection(stringResource(R.string.pine_diag_section_network)) {
            PineOptionRow(stringResource(R.string.pine_diag_net_type), "\uf1eb", viewState.netType)
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            PineOptionRow(stringResource(R.string.pine_diag_net_connected), "\uf1eb", viewState.netConnected)
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            PineOptionRow(stringResource(R.string.pine_diag_net_internet), "\uf1eb", viewState.netHasInternet)
        }

        // Storage
        DiagSection(stringResource(R.string.pine_diag_section_storage)) {
            PineOptionRow(stringResource(R.string.pine_diag_storage_total), "\uf0a0", viewState.storageTotal)
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            PineOptionRow(stringResource(R.string.pine_diag_storage_avail), "\uf0a0", viewState.storageAvail)
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            PineOptionRow(stringResource(R.string.pine_diag_cache_size), "\uf0a0", viewState.cacheSizeMb)
        }

        // Device
        DiagSection(stringResource(R.string.pine_diag_section_device)) {
            PineOptionRow(stringResource(R.string.pine_diag_device_model), "\uf10b", viewState.deviceModel)
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            PineOptionRow(stringResource(R.string.pine_diag_manufacturer), "\uf10b", viewState.manufacturer)
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            PineOptionRow(stringResource(R.string.pine_diag_android_ver), "\uf10b", viewState.androidVersion)
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            PineOptionRow(stringResource(R.string.pine_diag_api_level), "\uf10b", viewState.apiLevel)
        }
    }
}

@Composable
private fun DiagSection(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp),
        )
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                content()
            }
        }
    }
}
