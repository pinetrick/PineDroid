package com.pine.pinedroid.language

import java.util.Locale

/**
 * 语言信息数据类
 */
open class LanguageInfo(
    open val code: String,           // 语言代码
    open val locale: Locale,         // 对应的Locale
    open val displayName: String,    // 显示名称
    open val nativeName: String      // 本地语言名称
) {
    fun toSupportedLanguages(): SupportedLanguages {
        // 先查表
        val matched = SupportedLanguages.getLanguageInfo(code)
        if (matched != null) {
            return matched
        }
        return SupportedLanguages.English.Default
    }
}