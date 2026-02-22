package com.pine.pindroidpp.demo_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items as lazyColumnItems
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pine.pinedroid.jetpack.ui.list.PineHorizontalChoiceBar
import com.pine.pinedroid.jetpack.ui.list.shopping.PineShoppingItemBean
import com.pine.pinedroid.jetpack.ui.list.shopping.PineShoppingListItemHorizontal
import com.pine.pinedroid.jetpack.ui.list.shopping.PineShoppingListItemVertical
import com.pine.pinedroid.jetpack.ui.list.staggered_grid.PineLazyVerticalStaggeredGrid
import com.pine.pinedroid.jetpack.ui.nav.PineGeneralScreen
import com.pine.pinedroid.jetpack.ui.nav.PineTopAppBar
import com.pine.pinedroid.jetpack.ui.search.no_record.PineSearchNoResult
import com.pine.pinedroid.jetpack.viewmodel.HandleNavigation

@Composable
fun DemoScreen(
    navController: NavController? = null,
    viewModel: DemoScreenVM = viewModel()
) {
    val viewState by viewModel.viewState.collectAsState()

    HandleNavigation(navController = navController, viewModel = viewModel) {
        viewModel.onInit()
    }

    PineGeneralScreen(
        title = {
            PineTopAppBar(
                title = "Shopping List Demo",
                onReturn = viewModel::navigateBack
            )
        },
        content = {
            DemoContent(viewModel, viewState)
        },
    )
}

@Composable
fun DemoContent(viewModel: DemoScreenVM, viewState: DemoScreenState) {
    Column(modifier = Modifier.fillMaxSize()) {
        PineHorizontalChoiceBar(
            horizontalChoiceBar = listOf("Grid View", "List View"),
            selectedPage = viewState.selectedTab,
            onPageChoice = viewModel::onTabChange
        )

        when (viewState.selectedTab) {
            0 -> VerticalGridView(viewState.shoppingItems)
            1 -> HorizontalListView(viewState.shoppingItems)
        }
    }
}

@Composable
private fun VerticalGridView(items: List<PineShoppingItemBean>) {
    if (items.isEmpty()) {
        PineSearchNoResult()
        return
    }
    PineLazyVerticalStaggeredGrid(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalItemSpacing = 4.dp
    ) {
        items(items) { item ->
            PineShoppingListItemVertical(
                shoppingItemBean = item,
                onItemClick = {}
            )
        }
    }
}

@Composable
private fun HorizontalListView(items: List<PineShoppingItemBean>) {
    if (items.isEmpty()) {
        PineSearchNoResult()
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        lazyColumnItems(items) { item ->
            PineShoppingListItemHorizontal(
                shoppingItemBean = item,
                onItemClick = {}
            )
        }
    }
}
