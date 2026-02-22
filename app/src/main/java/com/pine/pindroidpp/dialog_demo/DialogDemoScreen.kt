package com.pine.pindroidpp.dialog_demo

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pine.pinedroid.jetpack.ui.nav.PineGeneralScreen
import com.pine.pinedroid.jetpack.ui.nav.PineTopAppBar
import com.pine.pinedroid.jetpack.ui.widget.PineOptionRow
import com.pine.pinedroid.jetpack.viewmodel.HandleNavigation

@Composable
fun DialogDemoScreen(
    navController: NavController? = null,
    viewModel: DialogDemoScreenVM = viewModel()
) {
    val viewState by viewModel.viewState.collectAsState()

    HandleNavigation(navController = navController, viewModel = viewModel) {}

    PineGeneralScreen(
        title = {
            PineTopAppBar(
                title = "Dialog Demo",
                onReturn = viewModel::navigateBack
            )
        },
        content = {
            DialogDemoContent(viewModel, viewState)
        },
    )
}

@Composable
fun DialogDemoContent(viewModel: DialogDemoScreenVM, viewState: DialogDemoScreenState) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {

        item {
            Text(
                text = "MessageBox",
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            HorizontalDivider()
        }

        item {
            PineOptionRow(
                title = "1-Button Dialog",
                icon = "\uf27a",
                description = "Simple confirmation message",
                onClick = viewModel::showMsgBox1
            )
        }
        item {
            PineOptionRow(
                title = "2-Button Dialog",
                icon = "\uf27a",
                description = "Confirm / Cancel choice",
                onClick = viewModel::showMsgBox2
            )
        }
        item {
            PineOptionRow(
                title = "3-Button Dialog",
                icon = "\uf27a",
                description = "Three options dialog",
                onClick = viewModel::showMsgBox3
            )
        }
        item {
            PineOptionRow(
                title = "4-Button Dialog",
                icon = "\uf27a",
                description = "Four options rating dialog",
                onClick = viewModel::showMsgBox4
            )
        }
        item {
            PineOptionRow(
                title = "Classic MessageBox API",
                icon = "\uf27a",
                description = "Callback-based (non-suspend) version",
                onClick = viewModel::showMsgBoxClassic
            )
        }

        item {
            Text(
                text = "Input",
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            HorizontalDivider()
        }

        item {
            PineOptionRow(
                title = "Bottom Edit Text",
                icon = "\uf303",
                description = "Bottom sheet text input dialog (suspend)",
                onClick = viewModel::showBottomInput
            )
        }

        item {
            Text(
                text = "Last Result",
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            HorizontalDivider()
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = viewState.lastResult,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
