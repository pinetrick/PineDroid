package com.pine.pinedroid.language

import java.util.Locale


/**
 * 支持的语言封装类
 */
sealed class SupportedLanguages : LanguageInfo("", Locale.getDefault(), "", "") {

    // 系统默认
    data object System : SupportedLanguages() {
        override val code = "system"
        override val locale = Locale.getDefault()
        override val displayName = "跟随系统"
        override val nativeName = "Follow System"
    }

    // 中文变体
    sealed class Chinese(code: String, locale: Locale, displayName: String, nativeName: String)
        : SupportedLanguages() {

        override val code = code
        override val locale = locale
        override val displayName = displayName
        override val nativeName = nativeName

        data object Simplified : Chinese("zh", Locale("zh", "CN"), "简体中文", "简体中文")
        data object SimplifiedChina : Chinese("zh-cn", Locale("zh", "CN"), "简体中文（中国）", "简体中文（中国）")
        data object SimplifiedHans : Chinese("zh-hans", Locale("zh", "CN"), "简体中文", "简体中文")
        data object TraditionalTaiwan : Chinese("zh-tw", Locale("zh", "TW"), "繁體中文（台灣）", "繁體中文（台灣）")
        data object TraditionalHongKong : Chinese("zh-hk", Locale("zh", "HK"), "繁體中文（香港）", "繁體中文（香港）")
        data object SimplifiedSingapore : Chinese("zh-sg", Locale("zh", "SG"), "简体中文（新加坡）", "简体中文（新加坡）")
    }

    // 英文变体
    sealed class English(code: String, locale: Locale, displayName: String, nativeName: String)
        : SupportedLanguages() {

        override val code = code
        override val locale = locale
        override val displayName = displayName
        override val nativeName = nativeName

        data object Default : English("en", Locale("en", "US"), "English", "English")
        data object US : English("en-us", Locale("en", "US"), "English (US)", "English (US)")
        data object UK : English("en-uk", Locale("en", "GB"), "English (UK)", "English (UK)")
        data object GB : English("en-gb", Locale("en", "GB"), "English (GB)", "English (GB)")
        data object Australia : English("en-au", Locale("en", "AU"), "English (Australia)", "English (Australia)")
        data object Canada : English("en-ca", Locale("en", "CA"), "English (Canada)", "English (Canada)")
        data object NewZealand : English("en-nz", Locale("en", "NZ"), "English (New Zealand)", "English (New Zealand)")
    }

    // 亚洲语言
    sealed class Asian(code: String, locale: Locale, displayName: String, nativeName: String)
        : SupportedLanguages() {

        override val code = code
        override val locale = locale
        override val displayName = displayName
        override val nativeName = nativeName

        data object Japanese : Asian("ja", Locale("ja", "JP"), "日语", "日本語")
        data object Korean : Asian("ko", Locale("ko", "KR"), "韩语", "한국어")
        data object Thai : Asian("th", Locale("th", "TH"), "泰语", "ไทย")
        data object Vietnamese : Asian("vi", Locale("vi", "VN"), "越南语", "Tiếng Việt")
        data object Indonesian : Asian("id", Locale("id", "ID"), "印尼语", "Bahasa Indonesia")
        data object Malay : Asian("ms", Locale("ms", "MY"), "马来语", "Bahasa Melayu")
        data object Filipino : Asian("tl", Locale("tl", "PH"), "菲律宾语", "Filipino")
        data object Hindi : Asian("hi", Locale("hi", "IN"), "印地语", "हिन्दी")
        data object Bengali : Asian("bn", Locale("bn", "BD"), "孟加拉语", "বাংলা")
        data object Tamil : Asian("ta", Locale("ta", "IN"), "泰米尔语", "தமிழ்")
        data object Telugu : Asian("te", Locale("te", "IN"), "泰卢固语", "తెలుగు")
        data object Malayalam : Asian("ml", Locale("ml", "IN"), "马拉雅拉姆语", "മലയാളം")
        data object Kannada : Asian("kn", Locale("kn", "IN"), "卡纳达语", "ಕನ್ನಡ")
        data object Gujarati : Asian("gu", Locale("gu", "IN"), "古吉拉特语", "ગુજરાતી")
        data object Punjabi : Asian("pa", Locale("pa", "IN"), "旁遮普语", "ਪੰਜਾਬੀ")
        data object Marathi : Asian("mr", Locale("mr", "IN"), "马拉地语", "मराठी")
        data object Nepali : Asian("ne", Locale("ne", "NP"), "尼泊尔语", "नेपाली")
        data object Sinhala : Asian("si", Locale("si", "LK"), "僧伽罗语", "සිංහල")
        data object Myanmar : Asian("my", Locale("my", "MM"), "缅甸语", "မြန်မာ")
        data object Khmer : Asian("km", Locale("km", "KH"), "高棉语", "ខ្មែរ")
        data object Lao : Asian("lo", Locale("lo", "LA"), "老挝语", "ລາວ")
    }

    // 欧洲语言
    sealed class European(code: String, locale: Locale, displayName: String, nativeName: String)
        : SupportedLanguages() {

        override val code = code
        override val locale = locale
        override val displayName = displayName
        override val nativeName = nativeName

        data object French : European("fr", Locale("fr", "FR"), "法语", "Français")
        data object German : European("de", Locale("de", "DE"), "德语", "Deutsch")
        data object Spanish : European("es", Locale("es", "ES"), "西班牙语", "Español")
        data object Italian : European("it", Locale("it", "IT"), "意大利语", "Italiano")
        data object Portuguese : European("pt", Locale("pt", "PT"), "葡萄牙语", "Português")
        data object BrazilianPortuguese : European("pt-br", Locale("pt", "BR"), "巴西葡萄牙语", "Português (Brasil)")
        data object Russian : European("ru", Locale("ru", "RU"), "俄语", "Русский")
        data object Dutch : European("nl", Locale("nl", "NL"), "荷兰语", "Nederlands")
        data object Swedish : European("sv", Locale("sv", "SE"), "瑞典语", "Svenska")
        data object Danish : European("da", Locale("da", "DK"), "丹麦语", "Dansk")
        data object Norwegian : European("no", Locale("no", "NO"), "挪威语", "Norsk")
        data object Finnish : European("fi", Locale("fi", "FI"), "芬兰语", "Suomi")
        data object Polish : European("pl", Locale("pl", "PL"), "波兰语", "Polski")
        data object Czech : European("cs", Locale("cs", "CZ"), "捷克语", "Čeština")
        data object Hungarian : European("hu", Locale("hu", "HU"), "匈牙利语", "Magyar")
        data object Greek : European("el", Locale("el", "GR"), "希腊语", "Ελληνικά")
        data object Turkish : European("tr", Locale("tr", "TR"), "土耳其语", "Türkçe")
        data object Bulgarian : European("bg", Locale("bg", "BG"), "保加利亚语", "Български")
        data object Romanian : European("ro", Locale("ro", "RO"), "罗马尼亚语", "Română")
        data object Croatian : European("hr", Locale("hr", "HR"), "克罗地亚语", "Hrvatski")
        data object Serbian : European("sr", Locale("sr", "RS"), "塞尔维亚语", "Српски")
        data object Slovak : European("sk", Locale("sk", "SK"), "斯洛伐克语", "Slovenčina")
        data object Slovenian : European("sl", Locale("sl", "SI"), "斯洛文尼亚语", "Slovenščina")
        data object Estonian : European("et", Locale("et", "EE"), "爱沙尼亚语", "Eesti")
        data object Latvian : European("lv", Locale("lv", "LV"), "拉脱维亚语", "Latviešu")
        data object Lithuanian : European("lt", Locale("lt", "LT"), "立陶宛语", "Lietuvių")
        data object Icelandic : European("is", Locale("is", "IS"), "冰岛语", "Íslenska")
        data object Maltese : European("mt", Locale("mt", "MT"), "马耳他语", "Malti")
    }

    // 中东和非洲语言
    sealed class MiddleEasternAndAfrican(code: String, locale: Locale, displayName: String, nativeName: String)
        : SupportedLanguages() {

        override val code = code
        override val locale = locale
        override val displayName = displayName
        override val nativeName = nativeName

        data object Arabic : MiddleEasternAndAfrican("ar", Locale("ar", "SA"), "阿拉伯语", "العربية")
        data object Hebrew : MiddleEasternAndAfrican("he", Locale("he", "IL"), "希伯来语", "עברית")
        data object Persian : MiddleEasternAndAfrican("fa", Locale("fa", "IR"), "波斯语", "فارسی")
        data object Urdu : MiddleEasternAndAfrican("ur", Locale("ur", "PK"), "乌尔都语", "اردو")
        data object Georgian : MiddleEasternAndAfrican("ka", Locale("ka", "GE"), "格鲁吉亚语", "ქართული")
        data object Amharic : MiddleEasternAndAfrican("am", Locale("am", "ET"), "阿姆哈拉语", "አማርኛ")
        data object Swahili : MiddleEasternAndAfrican("sw", Locale("sw", "TZ"), "斯瓦希里语", "Kiswahili")
        data object Zulu : MiddleEasternAndAfrican("zu", Locale("zu", "ZA"), "祖鲁语", "IsiZulu")
        data object Afrikaans : MiddleEasternAndAfrican("af", Locale("af", "ZA"), "南非语", "Afrikaans")
    }

    companion object {
        /**
         * 所有支持的语言列表
         */
        val SUPPORTED_LANGUAGES: List<SupportedLanguages> = listOf(
            // 系统默认
            System,

            // 中文变体
            Chinese.Simplified,
            Chinese.SimplifiedChina,
            Chinese.SimplifiedHans,
            Chinese.TraditionalTaiwan,
            Chinese.TraditionalHongKong,
            Chinese.SimplifiedSingapore,

            // 英文变体
            English.Default,
            English.US,
            English.UK,
            English.GB,
            English.Australia,
            English.Canada,
            English.NewZealand,

            // 亚洲语言
            Asian.Japanese,
            Asian.Korean,
            Asian.Thai,
            Asian.Vietnamese,
            Asian.Indonesian,
            Asian.Malay,
            Asian.Filipino,
            Asian.Hindi,
            Asian.Bengali,
            Asian.Tamil,
            Asian.Telugu,
            Asian.Malayalam,
            Asian.Kannada,
            Asian.Gujarati,
            Asian.Punjabi,
            Asian.Marathi,
            Asian.Nepali,
            Asian.Sinhala,
            Asian.Myanmar,
            Asian.Khmer,
            Asian.Lao,

            // 欧洲语言
            European.French,
            European.German,
            European.Spanish,
            European.Italian,
            European.Portuguese,
            European.BrazilianPortuguese,
            European.Russian,
            European.Dutch,
            European.Swedish,
            European.Danish,
            European.Norwegian,
            European.Finnish,
            European.Polish,
            European.Czech,
            European.Hungarian,
            European.Greek,
            European.Turkish,
            European.Bulgarian,
            European.Romanian,
            European.Croatian,
            European.Serbian,
            European.Slovak,
            European.Slovenian,
            European.Estonian,
            European.Latvian,
            European.Lithuanian,
            European.Icelandic,
            European.Maltese,

            // 中东和非洲语言
            MiddleEasternAndAfrican.Arabic,
            MiddleEasternAndAfrican.Hebrew,
            MiddleEasternAndAfrican.Persian,
            MiddleEasternAndAfrican.Urdu,
            MiddleEasternAndAfrican.Georgian,
            MiddleEasternAndAfrican.Amharic,
            MiddleEasternAndAfrican.Swahili,
            MiddleEasternAndAfrican.Zulu,
            MiddleEasternAndAfrican.Afrikaans,
        )

        // 创建查找Map以提高查找效率
        private val languageMap = SUPPORTED_LANGUAGES.associateBy { it.code.lowercase() }

        /**
         * 根据语言代码获取语言信息
         */
        fun getLanguageInfo(code: String): SupportedLanguages? {
            return languageMap[code.lowercase()]
        }

        /**
         * 根据语言代码获取Locale
         */
        fun getLocale(code: String): Locale {
            return getLanguageInfo(code)?.locale ?: run {
                // 如果没有找到，尝试解析
                try {
                    val normalizedCode = code.lowercase()
                    val parts = normalizedCode.split("-", "_")
                    when (parts.size) {
                        1 -> Locale(parts[0])
                        2 -> Locale(parts[0], parts[1].uppercase())
                        else -> Locale.getDefault()
                    }
                } catch (e: Exception) {
                    Locale.getDefault()
                }
            }
        }

        /**
         * 获取显示名称
         */
        fun getDisplayName(code: String): String {
            return getLanguageInfo(code)?.displayName ?: run {
                try {
                    val locale = getLocale(code)
                    locale.getDisplayLanguage(Locale.getDefault()).replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase() else it.toString()
                    }
                } catch (e: Exception) {
                    code.uppercase()
                }
            }
        }

        /**
         * 获取本地语言名称
         */
        fun getNativeName(code: String): String {
            return getLanguageInfo(code)?.nativeName ?: getDisplayName(code)
        }

        /**
         * 检查是否支持某个语言
         */
        fun isSupported(code: String): Boolean {
            return languageMap.containsKey(code.lowercase()) || code.lowercase() == "system"
        }

        /**
         * 获取所有支持的语言代码
         */
        fun getAllSupportedCodes(): List<String> {
            return SUPPORTED_LANGUAGES.map { it.code }
        }

        /**
         * 获取常用语言（可以根据需要调整）
         */
        fun getCommonLanguages(): List<SupportedLanguages> {
            return SUPPORTED_LANGUAGES.filter {
                it.code in listOf("system", "en", "zh", "hi", "es")
            }
        }
    }
}