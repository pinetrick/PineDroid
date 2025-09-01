package com.pine.pinedroid.language

import android.app.Activity
import android.content.res.AssetManager
import android.content.res.Configuration
import android.os.Build
import com.pine.pinedroid.utils.activityContext
import com.pine.pinedroid.utils.appContext
import com.pine.pinedroid.utils.sp
import java.util.Locale

object LanguageManager {
    private const val KEY_LANGUAGE = "selected_language"

    val APP_SUPPORTED_LANGUAGE: List<Locale>
        get() {
            val locales = mutableListOf<Locale>()
            try {
                val method = AssetManager::class.java.getDeclaredMethod("getLocales")
                val result = method.invoke(appContext.assets) as Array<String>
                for (tag in result) {
                    if (tag.isBlank()) continue
                    try {
                        locales.add(Locale.forLanguageTag(tag.replace("_", "-")))
                    } catch (_: Exception) {
                    }
                }
            } catch (_: Exception) {
            }
            return locales
        }

    fun getCurrentAppLocale(): Locale {
        val config: Configuration = appContext.resources.configuration
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.locales.get(0)
        } else {
            @Suppress("DEPRECATION")
            config.locale
        }
    }

    fun saveLanguage(code: String) {
        // 存储小写
        sp(KEY_LANGUAGE, code.lowercase())
        applyLanguage(code)
    }

    fun applySavedLanguage(){
        applyLanguage(getSavedLanguage())
    }

    fun applyLanguage(code: String) {
        val locale = if (code.contains("-")) {
            val parts = code.split("-")
            Locale(parts[0], parts[1])
        } else {
            Locale(code)
        }

        Locale.setDefault(locale)

        val resources = activityContext.resources
        val config = Configuration(resources.configuration)

        config.setLocale(locale)
        activityContext.createConfigurationContext(config)
        appContext = appContext.createConfigurationContext(config)


        // 更新状态，通知 UI 刷新
        _appLocaleResource.value = appContext.resources
    }


    fun getSavedLanguage(): String {
        val language: String? = sp(KEY_LANGUAGE)
        if (language != null) return language

        // 如果没有存储过，就返回 app 当前实际使用的语言
        return getCurrentAppLocale().language
    }





}