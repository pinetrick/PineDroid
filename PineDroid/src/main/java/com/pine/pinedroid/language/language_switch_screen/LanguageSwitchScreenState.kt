package com.pine.pinedroid.language.language_switch_screen

import com.pine.pinedroid.language.LanguageInfo
import com.pine.pinedroid.language.SupportedLanguages


data class LanguageSwitchScreenState(
    var currentLanguage: LanguageInfo = SupportedLanguages.SUPPORTED_LANGUAGES.first(),
    var supportedLanguages: List<LanguageInfo> = SupportedLanguages.SUPPORTED_LANGUAGES

){}