package com.pine.pinedroid.jetpack.ui.list.vertical_grid


import android.content.res.Configuration
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pine.pinedroid.jetpack.ui.list.shopping.PineShoppingItemBean
import com.pine.pinedroid.jetpack.ui.list.shopping.PineShoppingItemBean.Companion.ShoppingItemBeanDemo
import com.pine.pinedroid.jetpack.ui.list.shopping.PineShoppingListItemVertical
import com.pine.pinedroid.jetpack.ui.search.no_record.PineSearchNoResult

/*
* ScoreBar must make sure every element has same high */
@Composable
fun PineLazyVerticalGrid(
    modifier: Modifier = Modifier,
    columns: GridCells = GridCells.Adaptive(150.dp),
    state: LazyGridState = rememberLazyGridState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical =
        if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    overscrollEffect: OverscrollEffect? = rememberOverscrollEffect(),
    content: LazyGridScope.() -> Unit,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterEnd
    ) {
        LazyVerticalGrid(
            columns = columns,
            modifier = Modifier.fillMaxSize(),
            state = state,
            contentPadding = contentPadding,
            reverseLayout = reverseLayout,
            flingBehavior = flingBehavior,
            verticalArrangement = verticalArrangement,
            userScrollEnabled = userScrollEnabled,
            overscrollEffect = overscrollEffect,
            horizontalArrangement = horizontalArrangement,
            content = content,
        )
    }

}


@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 800,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
private fun PreviewGrid() {


    PineLazyVerticalGrid(
        contentPadding = PaddingValues(3.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp),
        columns = GridCells.Adaptive(150.dp),
        modifier = Modifier.background(Color.Transparent.copy(alpha = 0.1f))
    ) {

        items(ShoppingItemBeanDemo) { destination ->
            PineShoppingListItemVertical(
                shoppingItemBean = destination,
                onItemClick = {  },
                modifier = Modifier.fillMaxHeight(2f)
            )
        }
    }
}