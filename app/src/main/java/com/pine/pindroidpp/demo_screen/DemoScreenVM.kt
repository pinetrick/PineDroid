package com.pine.pindroidpp.demo_screen

import com.pine.pinedroid.jetpack.viewmodel.BaseViewModel

class DemoScreenVM : BaseViewModel<DemoScreenState>(DemoScreenState::class) {

    fun onInit() {
    }

    fun onTabChange(index: Int) {
        setStateSync { copy(selectedTab = index) }
    }
}
