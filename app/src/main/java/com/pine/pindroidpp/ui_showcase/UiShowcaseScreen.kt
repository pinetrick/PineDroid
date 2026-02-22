package com.pine.pindroidpp.ui_showcase

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pine.pinedroid.jetpack.ui.PineRatingStar
import com.pine.pinedroid.jetpack.ui.button.PineButton
import com.pine.pinedroid.jetpack.ui.font.PineIcon
import com.pine.pinedroid.jetpack.ui.font.PineIconButton
import com.pine.pinedroid.jetpack.ui.list.PineHorizontalChoiceBar
import com.pine.pinedroid.jetpack.ui.nav.PineGeneralScreen
import com.pine.pinedroid.jetpack.ui.nav.PineTopAppBar
import com.pine.pinedroid.jetpack.ui.search.PineSearchTextView
import com.pine.pinedroid.jetpack.ui.text.PineAnimatedNumberText
import com.pine.pinedroid.jetpack.ui.user.profile.PineProfileView
import com.pine.pinedroid.jetpack.ui.value_drag_control.PineValueDragControl
import com.pine.pinedroid.jetpack.viewmodel.HandleNavigation

@Composable
fun UiShowcaseScreen(
    navController: NavController? = null,
    viewModel: UiShowcaseScreenVM = viewModel()
) {
    val viewState by viewModel.viewState.collectAsState()

    HandleNavigation(navController = navController, viewModel = viewModel) {
        viewModel.onInit()
    }

    PineGeneralScreen(
        title = {
            PineTopAppBar(
                title = "UI Components",
                onReturn = viewModel::navigateBack
            )
        },
        content = {
            UiShowcaseContent(viewModel, viewState)
        },
    )
}

@Composable
fun UiShowcaseContent(viewModel: UiShowcaseScreenVM, viewState: UiShowcaseScreenState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {

        // === BUTTONS ===
        item { ShowcaseSectionHeader("PineButton") }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PineButton(text = "Primary", onClick = {})
                PineButton(icon = "\uf015", text = "Home", onClick = {})
                PineButton(text = "Disabled", enabled = false, onClick = {})
            }
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PineButton(icon = "\uf004", border = null, onClick = {})
                PineButton(icon = "\uf1f8", text = "Delete", onClick = {})
                PineButton(icon = "\uf0c7", text = "Save", onClick = {})
                PineButton(icon = "\uf00c", text = "Done", onClick = {})
            }
        }

        // === ICON BUTTONS ===
        item { ShowcaseSectionHeader("PineIconButton") }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PineIconButton(text = "\uf015", onClick = {})
                PineIconButton(text = "\uf007", onClick = {})
                PineIconButton(text = "\uf002", onClick = {})
                PineIconButton(text = "\uf0e0", onClick = {})
                PineIconButton(text = "\uf3c5", onClick = {})
                PineIconButton(text = "\uf0f3", onClick = {})
                PineIconButton(text = "\uf013", onClick = {})
            }
        }

        // === FONTAWESOME ICONS ===
        item { ShowcaseSectionHeader("FontAwesome Icons (PineIcon)") }
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val iconsRow1 = listOf(
                        "\uf015" to "Home",
                        "\uf007" to "User",
                        "\uf004" to "Heart",
                        "\uf005" to "Star",
                        "\uf002" to "Search",
                    )
                    iconsRow1.forEach { (icon, label) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            PineIcon(
                                text = icon,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 22.sp
                            )
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val iconsRow2 = listOf(
                        "\uf3c5" to "Pin",
                        "\uf0e0" to "Email",
                        "\uf095" to "Phone",
                        "\uf0f3" to "Bell",
                        "\uf1f8" to "Trash",
                    )
                    iconsRow2.forEach { (icon, label) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            PineIcon(
                                text = icon,
                                color = MaterialTheme.colorScheme.secondary,
                                fontSize = 22.sp
                            )
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val iconsRow3 = listOf(
                        "\uf030" to "Camera",
                        "\uf03e" to "Image",
                        "\uf0c7" to "Save",
                        "\uf013" to "Settings",
                        "\uf01e" to "Refresh",
                    )
                    iconsRow3.forEach { (icon, label) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            PineIcon(
                                text = icon,
                                color = MaterialTheme.colorScheme.tertiary,
                                fontSize = 22.sp
                            )
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // === RATING STARS ===
        item { ShowcaseSectionHeader("PineRatingStar  (rating: ${viewState.rating})") }
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(horizontal = 24.dp)
            ) {
                PineRatingStar(
                    rating = viewState.rating,
                    onRatingChange = viewModel::onRatingChange
                )
            }
        }

        // === ANIMATED NUMBER ===
        item { ShowcaseSectionHeader("PineAnimatedNumberText") }
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PineAnimatedNumberText(
                    targetValue = viewState.animatedTarget,
                    prefix = "$",
                    suffix = " USD",
                    precision = 2,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 36.sp,
                    durationMillis = 1200,
                )
                Spacer(Modifier.height(8.dp))
                PineButton(
                    text = "Animate Again",
                    icon = "\uf01e",
                    onClick = viewModel::onAnimateAgain
                )
            }
        }

        // === VALUE DRAG CONTROL ===
        item { ShowcaseSectionHeader("PineValueDragControl") }
        item {
            PineValueDragControl(
                value = viewState.sliderValue,
                onValueChange = viewModel::onSliderChange,
                valueRange = 0f..100f,
                title = "Brightness",
                valueSuffix = "%",
                quickValues = listOf(25f, 50f, 75f, 100f)
            )
        }

        // === HORIZONTAL CHOICE BAR ===
        item { ShowcaseSectionHeader("PineHorizontalChoiceBar") }
        item {
            PineHorizontalChoiceBar(
                horizontalChoiceBar = listOf("All", "Recent", "Popular", "Favorites", "Trending"),
                selectedPage = viewState.selectedTab,
                onPageChoice = viewModel::onTabChange,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
        item {
            Text(
                text = "Selected: ${listOf("All","Recent","Popular","Favorites","Trending")[viewState.selectedTab]}",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // === SEARCH BAR ===
        item { ShowcaseSectionHeader("PineSearchTextView") }
        item {
            PineSearchTextView(
                searchText = viewState.searchText,
                placeHolder = "Search anything...",
                onTextChange = viewModel::onSearchChange,
                locationIcon = viewState.searchText.isEmpty(),
                onPhotoPickup = {},
                onTakePhoto = {},
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        // === PROFILE VIEW ===
        item { ShowcaseSectionHeader("PineProfileView") }
        item {
            PineProfileView(
                name = if (viewState.isLoggedIn) "Pine User" else null,
                onLogin = viewModel::onLoginToggle,
                onLogout = viewModel::onLoginToggle,
            )
        }
    }
}

@Composable
fun ShowcaseSectionHeader(title: String) {
    Column {
        Spacer(Modifier.height(8.dp))
        Text(
            text = title,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
    }
}
