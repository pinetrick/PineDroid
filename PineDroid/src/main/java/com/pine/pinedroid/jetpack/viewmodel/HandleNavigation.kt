package com.pine.pinedroid.jetpack.viewmodel

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.pine.pinedroid.jetpack.ui.PineSyncSystemNavBarWithBottomBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun <T: Any> HandleNavigation(
    navController: NavController?,
    viewModel: BaseViewModel<T>,
    runOnceBlock: (suspend () -> Unit)? = null,
) {
    val context = LocalContext.current

    // 当有未保存的更改时拦截返回
    BackHandler(enabled = true) {
        viewModel.onReturnClick()
    }

    LaunchedEffect(Unit) {
        viewModel.navEvents.collect { event ->
            when (event) {
                is NavEvent.Navigate -> {
                    try {
                        if (event.popUpThis) {
                            navController?.popBackStack()
                        }

                        // 处理 removeFromHistoryIfExists 逻辑
                        if (event.rebuildScreenIfExist) {
                            // 获取当前 backStack 的快照值
                            val backStack = navController?.currentBackStack?.value
                            backStack?.find { entry ->
                                entry.destination.route == event.route
                            }?.destination?.route?.let { route ->
                                navController?.popBackStack(route, true)
                            }
                        }

                        navController?.navigate(event.route)
                    } catch (_: Exception) {
                    }
                }

                is NavEvent.NavigateWithArgs -> {
                    // 构建带参数的路由
                    val routeWithArgs = buildRouteWithArgs(event.route, event.args)
                    navController?.navigate(routeWithArgs)
                }

                is NavEvent.NavigateBack -> {
                    if (navController?.popBackStack() == false) {
                        // 没有更多可返回的，退出当前 Activity
                        (context as? Activity)?.finish()
                    }
                }

                is NavEvent.PopUpTo -> {
                    navController?.popBackStack(event.route, event.inclusive)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        runOnceBlock?.let { runOnceBlock ->
            viewModel.runOnce {
                viewModel.viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        runOnceBlock()
                    }
                }
            }
        }
    }

    PineSyncSystemNavBarWithBottomBar()
}

// 构建带参数的路由
private fun buildRouteWithArgs(route: String, args: Map<String, Any>): String {
    if (args.isEmpty()) return route

    val argString = args.entries.joinToString("&") { (key, value) ->
        "$key=${value.toString().urlEncode()}"
    }
    return "$route?$argString"
}

private fun String.urlEncode(): String {
    return java.net.URLEncoder.encode(this, "UTF-8")
}