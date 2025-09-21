package com.pine.pinedroid.language.language_switch_screen


import com.pine.pinedroid.jetpack.viewmodel.BaseViewModel
import com.pine.pinedroid.language.LanguageInfo
import com.pine.pinedroid.language.LanguageManager


class LanguageSwitchScreenVM :
    BaseViewModel<LanguageSwitchScreenState>(LanguageSwitchScreenState::class) {

    fun onInit() {
        setState {
            copy(
                currentLanguage = LanguageManager.getSavedLanguage()
            )
        }
    }


    fun languageChoice(language: LanguageInfo) {
        LanguageManager.saveLanguage(language.toSupportedLanguages())
    }
}