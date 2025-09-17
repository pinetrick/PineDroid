package com.pine.pinedroid.jetpack.ui.require_permission.enable_permission_screen

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.pine.pinedroid.jetpack.ui.widget.PineOptionRow
import com.pine.pinedroid.jetpack.ui.widget.PineOptionRowExt


@Composable
fun PermissionPineOptionRow(
    title: String,
    icon: String? = null,
    description: String? = null,
    hasPermission: Boolean = false,
    onClick: (() -> Unit)? = null,
){
    PineOptionRowExt(
        icon = icon,
        title = title,
        description = description,
        rightIcon = if(hasPermission) "\uf00c" else "\uf057",
        rightIconColor =  if(hasPermission) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary ,
        onClick = onClick
    )
}