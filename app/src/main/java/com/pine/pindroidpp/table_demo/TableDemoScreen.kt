package com.pine.pindroidpp.table_demo

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pine.pinedroid.jetpack.ui.PineZoomableTable
import com.pine.pinedroid.jetpack.ui.nav.PineGeneralScreen
import com.pine.pinedroid.jetpack.ui.nav.PineTopAppBar
import com.pine.pinedroid.jetpack.viewmodel.HandleNavigation

@Composable
fun TableDemoScreen(
    navController: NavController? = null,
    viewModel: TableDemoScreenVM = viewModel()
) {
    val viewState by viewModel.viewState.collectAsState()

    HandleNavigation(navController = navController, viewModel = viewModel) {
        viewModel.onInit()
    }

    PineGeneralScreen(
        title = {
            PineTopAppBar(
                title = "Zoomable Table  (pinch to zoom)",
                onReturn = viewModel::navigateBack
            )
        },
        content = {
            PineZoomableTable(
                columnCount = viewState.columns.size,
                lineCount = viewState.rows.size,
                header = { columnIndex, _ ->
                    Text(
                        text = viewState.columns[columnIndex],
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                },
                renderCell = { rowIndex, columnIndex, _ ->
                    Text(
                        text = viewState.rows[rowIndex][columnIndex],
                        style = MaterialTheme.typography.bodySmall,
                        color = if (rowIndex % 2 == 0)
                            MaterialTheme.colorScheme.onSurface
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            )
        },
    )
}
