package com.pine.pinedroid.language.language_switch_screen

import com.pine.pinedroid.language.LanguageInfo
import com.pine.pinedroid.language.SupportedLanguages


data class LanguageSwitchScreenState(
    var currentLanguage: LanguageInfo,
    var supportedLanguages: List<LanguageInfo> = SupportedLanguages.APP_SUPPORTED_LANGUAGE

){}