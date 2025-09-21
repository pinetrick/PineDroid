package com.pine.pinedroid.language.language_switch_screen


import androidx.lifecycle.viewModelScope
import com.pine.pinedroid.R
import com.pine.pinedroid.jetpack.viewmodel.BaseViewModel
import com.pine.pinedroid.language.LanguageInfo
import com.pine.pinedroid.language.LanguageManager
import com.pine.pinedroid.ui.message_box.MsgBox
import com.pine.pinedroid.utils.r_resource.stringResource
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference


class LanguageSwitchScreenVM :
    BaseViewModel<LanguageSwitchScreenState>(LanguageSwitchScreenState::class) {

    override fun navigateBack() {
        viewModelScope.launch {
            if (currentState.currentLanguage != LanguageManager.getSavedLanguage()) {
                val btn = MsgBox().invoke(R.string.pine_language_save_confirm_title.stringResource(), R.string.pine_language_save_confirm_btn1.stringResource(), R.string.pine_language_save_confirm_btn2.stringResource())
                if (btn == 1) {
                    return@launch saveLanguage()
                }
            }
            super.navigateBack()
        }
    }

    override fun getInitialViewState(): LanguageSwitchScreenState {
        return LanguageSwitchScreenState(
            supportedLanguages = SUPPORTED_LANGUAGES,
            currentLanguage = LanguageManager.getSavedLanguage()
        )
    }


    fun languageChoice(language: LanguageInfo) {
        setState {
            copy(currentLanguage = language)
        }
    }

    fun saveLanguage() {
        val currentLanguage = currentState.currentLanguage
        LanguageManager.saveLanguage(currentLanguage.toSupportedLanguages())
        onLanguageSaved.get()?.invoke(currentLanguage)
        navigateBack()
    }

    companion object {
        var SUPPORTED_LANGUAGES: List<LanguageInfo> = emptyList()
        var onLanguageSaved: WeakReference<((LanguageInfo) -> Unit)> = WeakReference(null)

    }
}