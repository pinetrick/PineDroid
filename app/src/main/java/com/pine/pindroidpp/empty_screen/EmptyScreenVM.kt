package com.pine.pindroidpp.empty_screen


import com.pine.pindroidpp.root.RootScreenState
import com.pine.pinedroid.jetpack.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class EmptyScreenVM : BaseViewModel<EmptyScreenState>(EmptyScreenState::class) {

    suspend fun onInit() {

    }
}