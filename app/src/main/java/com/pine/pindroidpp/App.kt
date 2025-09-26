package com.pine.pindroidpp

import android.app.Application
import com.pine.pinedroid.PineConfig
import com.pine.pinedroid.debug.window.PineDebugWindow
import com.pine.pinedroid.language.SupportedLanguages.Companion.SUPPORTED_LANGUAGES
import com.pine.pinedroid.language.language_switch_screen.LanguageSwitchScreenVM

class App: Application()
{
    override fun onCreate() {
        super.onCreate()
        PineDebugWindow.setIsDebugWindowAlwaysOn(true)

        LanguageSwitchScreenVM.SUPPORTED_LANGUAGES = SUPPORTED_LANGUAGES.filter {
            it.code in listOf("system", "en", "zh", "hi", "es", "de", "fr", "ja", "ko")
        }

    }
}