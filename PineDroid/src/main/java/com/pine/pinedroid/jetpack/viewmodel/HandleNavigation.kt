package com.pine.pinedroid.jetpack.viewmodel

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController

@Composable
fun HandleNavigation(
    navController: NavController?,
    viewModel: BaseViewModel
) {
    // 当有未保存的更改时拦截返回
    BackHandler(enabled = true) {
        viewModel::onReturnClick
    }

    LaunchedEffect(Unit) {
        viewModel.navEvents.collect { event ->
            when (event) {
                is NavEvent.Navigate -> {
                    navController?.navigate(event.route)
                }

                is NavEvent.NavigateWithArgs -> {
                    // 构建带参数的路由
                    val routeWithArgs = buildRouteWithArgs(event.route, event.args)
                    navController?.navigate(routeWithArgs)
                }

                is NavEvent.NavigateBack -> {
                    navController?.popBackStack()
                }

                is NavEvent.PopUpTo -> {
                    navController?.popBackStack(event.route, event.inclusive)
                }
            }
        }
    }
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