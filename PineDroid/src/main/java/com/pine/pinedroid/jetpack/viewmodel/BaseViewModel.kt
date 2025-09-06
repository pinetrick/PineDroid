package com.pine.pinedroid.jetpack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pine.pinedroid.utils.reflect.createInstance
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

// 简化的 ViewModel 基类
open class BaseViewModel<T: Any>(val clazz: KClass<T>): ViewModel() {
    private val _navEvents = MutableSharedFlow<NavEvent>()
    val navEvents: SharedFlow<NavEvent> = _navEvents

    protected val _viewState = MutableStateFlow(getInitialViewState())
    val viewState: StateFlow<T> = _viewState

    open fun getInitialViewState(): T{
        return createInstance(clazz)
    }

    private var initialized = false

    suspend fun runOnce(block: suspend () -> Unit) {
        if (!initialized) {
            initialized = true
            block()
        }
    }

    open fun onReturnClick(){
        navigateBack()

    }

    fun navigateTo(route: String, isPopThis: Boolean = false) {
        viewModelScope.launch {
            if (isPopThis) _navEvents.emit(NavEvent.NavigateBack)
            _navEvents.emit(NavEvent.Navigate(route))
        }
    }

    fun navigateWithArgs(route: String, args: Map<String, Any> = emptyMap()) {
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