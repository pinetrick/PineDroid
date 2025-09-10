package com.pine.pindroidpp.demo_screen

import com.pine.pinedroid.jetpack.ui.list.shopping.PineShoppingItemBean


data class DemoScreenState(
    var shoppingItems: List<PineShoppingItemBean> = PineShoppingItemBean.ShoppingItemBeanDemo

){}