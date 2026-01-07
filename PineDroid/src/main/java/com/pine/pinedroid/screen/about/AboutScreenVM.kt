package com.pine.pinedroid.screen.about

import com.pine.pinedroid.R
import com.pine.pinedroid.hardware.build.AppInfo
import com.pine.pinedroid.jetpack.viewmodel.BaseViewModel


class AboutScreenVM : BaseViewModel<AboutScreenState>(AboutScreenState::class) {

    suspend fun onInit() {
        setState {
            copy(
                versionCode = AppInfo.getVersionCode(),
                versionName = AppInfo.getVersionName(),
                buildDate = AppInfo.getBuildDate(),
                minSdkVersion = AppInfo.getMinSdkVersion(),
                appName = AppInfo.getAppName(),
            )

        }


    }

    companion object {
        var logo: Int = R.drawable.pine_droid_icon
        var copyRight: String = ""

    }
}