package com.pine.pindroidpp.demo_screen

import com.pine.pinedroid.jetpack.ui.list.shopping.PineShoppingItemBean
import com.pine.pinedroid.utils.shrinker_keep.Keep

@Keep
data class DemoScreenState(
    var shoppingItems: List<PineShoppingItemBean> = PineShoppingItemBean.ShoppingItemBeanDemo

){}