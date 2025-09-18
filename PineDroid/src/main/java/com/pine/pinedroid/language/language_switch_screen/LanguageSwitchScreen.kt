package com.pine.pinedroid.language.language_switch_screen

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pine.pinedroid.R
import com.pine.pinedroid.jetpack.ui.nav.PineGeneralScreen
import com.pine.pinedroid.jetpack.ui.nav.PineTopAppBar
import com.pine.pinedroid.jetpack.viewmodel.HandleNavigation
import com.pine.pinedroid.language.LanguageManager
import com.pine.pinedroid.language.getCurrentLanguageInfo

@Composable
fun LanguageSwitchScreen(
    navController: NavController? = null,
    viewModel: LanguageSwitchScreenVM = viewModel()
) {
    val viewState by viewModel.viewState.collectAsState()
    val context = LocalContext.current
    val currentLanguage = getCurrentLanguageInfo(context)

    HandleNavigation(navController = navController, viewModel = viewModel) {
        viewModel.onInit()
    }

    PineGeneralScreen(
        title = {
            PineTopAppBar(
                title = stringResource(R.string.language_settings),
                onReturn = viewModel::navigateBack
            )
        },
        content = {
            Content(viewModel, viewState, currentLanguage.code)
        },
    )
}

@Composable
fun Content(
    viewModel: LanguageSwitchScreenVM,
    viewState: LanguageSwitchScreenState,
    currentLanguageCode: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.select_language),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn {
            items(viewState.supportedLanguages) { language ->
                LanguageItem(
                    language = language,
                    isSelected = language.code == currentLanguageCode,
                    onLanguageSelected = { viewModel.onLanguageChoosed(language) }
                )
                Divider()
            }
        }
    }
}

@Composable
fun LanguageItem(
    language: LanguageInfo,
    isSelected: Boolean,
    onLanguageSelected: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onLanguageSelected() }
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = language.displayName)
            Text(
                text = language.nativeName,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        RadioButton(
            selected = isSelected,
            onClick = { onLanguageSelected() }
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 800,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun PreviewDark() {
    LanguageSwitchScreen()
}