package com.pine.pinedroid.jetpack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

// 简化的 ViewModel 基类
abstract class BaseViewModel : ViewModel() {
    private val _navEvents = MutableSharedFlow<NavEvent>()
    val navEvents: SharedFlow<NavEvent> = _navEvents

    protected fun navigateTo(route: String) {
        viewModelScope.launch {
            _navEvents.emit(NavEvent.Navigate(route))
        }
    }

    protected fun navigateWithArgs(route: String, args: Map<String, Any> = emptyMap()) {
        viewModelScope.launch {
            _navEvents.emit(NavEvent.NavigateWithArgs(route, args))
        }
    }

    fun navigateBack() {
        viewModelScope.launch {
            _navEvents.emit(NavEvent.NavigateBack)
        }
    }

    fun popUpTo(route: String, inclusive: Boolean = false) {
        viewModelScope.launch {
            _navEvents.emit(NavEvent.PopUpTo(route, inclusive))
        }
    }


}