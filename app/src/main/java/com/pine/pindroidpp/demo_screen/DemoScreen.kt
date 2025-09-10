package com.pine.pindroidpp.demo_screen


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pine.pinedroid.jetpack.ui.list.shopping.PineShoppingItemBean
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
fun Content(viewModel: DemoScreenVM, viewState: DemoScreenState) {
    ShoppingList(viewState.shoppingItems)
}


@Composable
private fun ShoppingList(
    destinations: List<PineShoppingItemBean>,
    onItemClick: (PineShoppingItemBean) -> Unit = {},
) {
    if (destinations.isEmpty()) return PineSearchNoResult()

    PineLazyVerticalStaggeredGrid(
        modifier = Modifier,
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalItemSpacing = 4.dp
    ) {

        items(destinations) { destination ->
            PineShoppingListItemVertical(
                shoppingItemBean = destination,
                onItemClick = onItemClick
            )
        }
    }
}

