package com.pine.pindroidpp.ui_showcase

import androidx.lifecycle.viewModelScope
import com.pine.pinedroid.jetpack.viewmodel.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UiShowcaseScreenVM : BaseViewModel<UiShowcaseScreenState>(UiShowcaseScreenState::class) {

    fun onInit() {
        setStateSync { copy(animatedTarget = 9876.54f) }
    }

    fun onRatingChange(rating: Float) {
        setStateSync { copy(rating = rating) }
    }

    fun onSliderChange(value: Float) {
        setStateSync { copy(sliderValue = value) }
    }

    fun onTabChange(index: Int) {
        setStateSync { copy(selectedTab = index) }
    }

    fun onSearchChange(text: String) {
        setStateSync { copy(searchText = text) }
    }

    fun onLoginToggle() {
        setStateSync { copy(isLoggedIn = !isLoggedIn) }
    }

    fun onAnimateAgain() = viewModelScope.launch {
        setState { copy(animatedTarget = null) }
        delay(100)
        val newValue = (1000..99999).random().toFloat() + (0..99).random() / 100f
        setState { copy(animatedTarget = newValue) }
    }
}
