package com.pine.pinedroid.language.language_switch_screen


import com.pine.pinedroid.jetpack.viewmodel.BaseViewModel
import com.pine.pinedroid.language.LanguageInfo
import com.pine.pinedroid.language.LanguageManager


class LanguageSwitchScreenVM : BaseViewModel<LanguageSwitchScreenState>(LanguageSwitchScreenState::class) {

    fun onInit() {

    }
    fun getCurrentLanguageInfo(): LanguageInfo {
        setState {
            copy(
                currentLanguage =
            )
        }
    }

    fun onLanguageChoosed(language: LanguageInfo){
        LanguageManager.saveLanguage(language.code)
    }
}