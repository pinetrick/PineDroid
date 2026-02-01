package com.pine.pinedroid.screen.permission.location


import androidx.activity.result.ActivityResultLauncher
import com.pine.pinedroid.jetpack.viewmodel.BaseViewModel
import com.pine.pinedroid.screen.permission.PinePermissionUtils


class EnableSpecifiedPermissionScreenVM :
    BaseViewModel<EnableSpecifiedPermissionScreenState>(EnableSpecifiedPermissionScreenState::class) {

    override fun getInitialViewState(): EnableSpecifiedPermissionScreenState{
        return EnableSpecifiedPermissionScreenState(
            permission = PinePermissionActivity.state.permission,
            icon = PinePermissionActivity.state.icon,
            text = PinePermissionActivity.state.text,
            description = PinePermissionActivity.state.description,
            optional = PinePermissionActivity.state.optional,
            isPermissionPermanentlyDenied = PinePermissionActivity.state.isPermissionPermanentlyDenied()
        )
    }

    override fun onReturnClick() {
        PinePermissionUtils.onPermission(false)
    }

    fun onResume() {
        if (PinePermissionActivity.state.hasPermission()) {
            PinePermissionUtils.onPermission(true)
        }
    }

    fun onPermissionResult(isGranted: Boolean) {
        setStateSync {
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