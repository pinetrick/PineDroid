package com.pine.pinedroid.language

import android.content.res.AssetManager
import android.content.res.Configuration
import android.os.Build
import com.pine.pinedroid.utils.activityContext
import com.pine.pinedroid.utils.appContext
import com.pine.pinedroid.utils.sp
import java.util.Locale

object LanguageManager {
    private const val KEY_LANGUAGE = "selected_language"

    /**
     * 获取当前 App 使用的语言（从配置里取）
     */
    fun getCurrentAppLanguage(): SupportedLanguages {
        val config: Configuration = appContext.resources.configuration
        val locale: Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.locales[0]
        } else {
            @Suppress("DEPRECATION")
            config.locale
        }
        return SupportedLanguages.getLanguageInfo(locale.toLanguageTag())
            ?: SupportedLanguages.System
    }

    /**
     * 保存并应用语言
     */
    fun saveLanguage(lang: SupportedLanguages) {
        sp(KEY_LANGUAGE, lang.code.lowercase())
        applyLanguage(lang)
    }

    /**
     * 应用保存的语言
     */
    fun applySavedLanguage() {
        applyLanguage(getSavedLanguage())
    }

    /**
     * 应用语言
     */
    fun applyLanguage(lang: SupportedLanguages) {
        val locale = lang.locale
        Locale.setDefault(locale)

        val resources = activityContext.resources
        val config = Configuration(resources.configuration)
        config.setLocale(locale)

        activityContext.createConfigurationContext(config)
        appContext = appContext.createConfigurationContext(config)

        // 更新状态，通知 UI 刷新
        _appLocaleResource.value = appContext.resources
    }

    /**
     * 获取保存的语言，没有就返回当前实际语言
     */
    fun getSavedLanguage(): SupportedLanguages {
        val code: String? = sp(KEY_LANGUAGE)
        return if (!code.isNullOrBlank()) {
            SupportedLanguages.getLanguageInfo(code) ?: SupportedLanguages.System
        } else {
            getCurrentAppLanguage()
        }
    }
}
