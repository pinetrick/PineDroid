package com.pine.pindroidpp.permission_test


import com.pine.pinedroid.screen.permission.PinePermissionUtils
import com.pine.pinedroid.screen.permission.one_permission.PineOnePermissionAlertWindow
import com.pine.pinedroid.screen.permission.one_permission.PineOnePermissionCamera
import com.pine.pinedroid.screen.permission.one_permission.PineOnePermissionCoarseLocation
import com.pine.pinedroid.screen.permission.one_permission.PineOnePermissionFineLocation
import com.pine.pinedroid.screen.permission.one_permission.PineOnePermissionForegroundServiceLocation
import com.pine.pinedroid.screen.permission.one_permission.PineOnePermissionReadExternalStorage
import com.pine.pinedroid.screen.permission.one_permission.PineOnePermissionReadMediaAudio
import com.pine.pinedroid.screen.permission.one_permission.PineOnePermissionReadMediaImages
import com.pine.pinedroid.screen.permission.one_permission.PineOnePermissionReadMediaVideo
import com.pine.pinedroid.screen.permission.one_permission.PineOnePermissionRecordAudio
import com.pine.pinedroid.screen.permission.one_permission.PineOnePermissionWriteExternalStorage
import com.pine.pinedroid.jetpack.viewmodel.BaseViewModel



class PermissionTestScreenVM :
    BaseViewModel<PermissionTestScreenState>(PermissionTestScreenState::class) {

    suspend fun onInit() {
        PinePermissionUtils.requestPermissions(
            listOf(
                PineOnePermissionWriteExternalStorage(),
                PineOnePermissionReadExternalStorage(),
                PineOnePermissionAlertWindow(),

                PineOnePermissionForegroundServiceLocation(),
                PineOnePermissionFineLocation(),
                PineOnePermissionCoarseLocation(true),
                PineOnePermissionCamera(),

                PineOnePermissionRecordAudio(),
                PineOnePermissionReadMediaVideo(),
                PineOnePermissionReadMediaAudio(),
                PineOnePermissionReadMediaImages(),

            ),
            ::onPermissionStateChanged
        )
    }

    fun onPermissionStateChanged(isMatchRequirement: Boolean) {
        setStateSync {
            copy(
                isLoading = false,
                hasPermission = isMatchRequirement
            )
        }
    }
}