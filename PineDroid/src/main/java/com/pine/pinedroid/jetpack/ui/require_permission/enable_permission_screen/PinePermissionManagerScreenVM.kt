package com.pine.pinedroid.jetpack.ui.require_permission.enable_permission_screen


import androidx.lifecycle.viewModelScope
import com.pine.pinedroid.hardware.permission.PineOnePermission
import com.pine.pinedroid.hardware.permission.PinePermissionUtils
import com.pine.pinedroid.hardware.permission.one_permission.PineOnePermissionReadExternalStorage
import com.pine.pinedroid.hardware.permission.one_permission.PineOnePermissionWriteExternalStorage
import com.pine.pinedroid.jetpack.viewmodel.BaseViewModel
import com.pine.pinedroid.utils.toast
import kotlinx.coroutines.launch


class PinePermissionManagerScreenVM :
    BaseViewModel<PinePermissionManagerScreenState>(PinePermissionManagerScreenState::class) {

    suspend fun onInit() {
        setState {
            copy(
                permissionList = REQUIRED_PERMISSION.map {
                    PermissionItem(
                        granted = it.hasPermission(),
                        permission = it
                    )
                },
            )
        }
    }

    fun requirePermission(
        permission: PineOnePermission
    ) = viewModelScope.launch {
         if (permission.hasPermission()) {
            return@launch
        }

        PinePermissionUtils.requestPermissions(listOf(permission)) {
            onInit()
        }

    }

    companion object {
        var REQUIRED_PERMISSION: List<PineOnePermission> = mutableListOf(
            PineOnePermissionReadExternalStorage(optional = true),
            PineOnePermissionWriteExternalStorage(optional = true),
        )
    }
}