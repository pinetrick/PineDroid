package com.pine.pinedroid.hardware.permission.location


import androidx.activity.result.ActivityResultLauncher
import com.pine.pinedroid.hardware.permission.PinePermissionUtils
import com.pine.pinedroid.jetpack.viewmodel.BaseViewModel


class EnableSpecifiedPermissionScreenVM :
    BaseViewModel<EnableSpecifiedPermissionScreenState>(EnableSpecifiedPermissionScreenState::class) {

    override fun onReturnClick(){
        PinePermissionUtils.onPermission(false)
    }

    fun onResume(){
        if (PinePermissionActivity.state.hasPermission()) {
            PinePermissionUtils.onPermission(true)
        }
    }

    fun onInit() {

        setState {
            copy(
                permission = PinePermissionActivity.state.permission,
                icon = PinePermissionActivity.state.icon,
                text = PinePermissionActivity.state.text,
                description = PinePermissionActivity.state.description,
                optional = PinePermissionActivity.state.optional,
                isPermissionPermanentlyDenied = PinePermissionActivity.state.isPermissionPermanentlyDenied()
            )
        }
    }

    fun onPermissionResult(isGranted: Boolean) {
        setState {
            copy(
                isPermissionPermanentlyDenied = PinePermissionActivity.state.isPermissionPermanentlyDenied()
            )
        }
        if (isGranted) {
            PinePermissionUtils.onPermission(true)
        } else {
            PinePermissionActivity.state.rememberPermissionDeny()
        }

    }

    fun grantPermissionFromSetting() {
        PinePermissionActivity.state.redirectToSettingAuth()
    }

    fun grantPermission(requestPermissionLauncher: ActivityResultLauncher<String>) {
        PinePermissionActivity.state.requirePermission(requestPermissionLauncher)
    }

    fun skipGrantPermission() {
        PinePermissionUtils.onPermission(false)

    }
}