package com.pine.pinedroid.jetpack.ui.list.shopping

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

// Bootstrap 5 风格配色
enum class ShoppingItemBeanLabelStyle(
    val textColor: Color,
    val backgroundColor: Color,
    val borderColor: Color? = null,
    val fontWeight: FontWeight = FontWeight.Normal
) {
    // Bootstrap Primary - 蓝色
    STYLE_PRIMARY(
        textColor = Color(0xFFFFFFFF), // Bootstrap: --bs-primary-text
        backgroundColor = Color(0xDD0D6EFD), // Bootstrap: --bs-primary
        fontWeight = FontWeight.Medium
    ),

    // Bootstrap Secondary - 灰色
    STYLE_SECONDARY(
        textColor = Color(0xFFFFFFFF), // Bootstrap: --bs-secondary-text
        backgroundColor = Color(0xDD6C757D), // Bootstrap: --bs-secondary
        fontWeight = FontWeight.Normal
    ),

    // Bootstrap Success - 绿色
    STYLE_SUCCESS(
        textColor = Color(0xFFFFFFFF), // Bootstrap: --bs-success-text
        backgroundColor = Color(0xDD198754), // Bootstrap: --bs-success
        fontWeight = FontWeight.Medium
    ),

    // Bootstrap Warning - 黄色（深色文字）
    STYLE_WARNING(
        textColor = Color(0xFF000000), // Bootstrap: 深色文字
        backgroundColor = Color(0xDDFFC107), // Bootstrap: --bs-warning
        fontWeight = FontWeight.SemiBold
    ),

    // Bootstrap Danger - 红色
    STYLE_DANGER(
        textColor = Color(0xFFFFFFFF), // Bootstrap: --bs-danger-text
        backgroundColor = Color(0xDDDC3545), // Bootstrap: --bs-danger
        fontWeight = FontWeight.Bold
    ),

    // Bootstrap Info - 青色
    STYLE_INFO(
        textColor = Color(0xFF000000), // Bootstrap: 深色文字
        backgroundColor = Color(0xDD0DCAF0), // Bootstrap: --bs-info
        fontWeight = FontWeight.Normal
    ),

    STYLE_HALF_TRANSPARENT(
        textColor = Color(0xFFFFFFFF),
        backgroundColor = Color(0x88000000),
        fontWeight = FontWeight.Normal
    ),

    // Bootstrap Light - 浅灰色
    STYLE_LIGHT(
        textColor = Color(0xFF000000), // Bootstrap: 深色文字
        backgroundColor = Color(0xDDF8F9FA), // Bootstrap: --bs-light
        borderColor = Color(0xFFDEE2E6), // Bootstrap: --bs-border-color
        fontWeight = FontWeight.Normal
    ),

    // Bootstrap Dark - 深灰色
    STYLE_DARK(
        textColor = Color(0xFFFFFFFF), // Bootstrap: --bs-dark-text
        backgroundColor = Color(0xDD212529), // Bootstrap: --bs-dark
        fontWeight = FontWeight.Medium
    ),

    // Bootstrap Premium (自定义) - 紫色
    STYLE_PREMIUM(
        textColor = Color(0xFFFFFFFF), // 白色文字
        backgroundColor = Color(0xDD6F42C1), // Bootstrap: Indigo-600
        fontWeight = FontWeight.Bold
    ),

    // Bootstrap Neutral (自定义) - 蓝灰色
    STYLE_NEUTRAL(
        textColor = Color(0xFFFFFFFF), // 白色文字
        backgroundColor = Color(0xDD6C757D), // Bootstrap Secondary 半透明
        fontWeight = FontWeight.Normal
    );

    // 扩展函数：根据数值获取枚举值
    companion object {
        fun fromValue(value: Int): ShoppingItemBeanLabelStyle {
            return values().getOrElse(value) { STYLE_NEUTRAL }
        }

        fun fromValue(value: Int?): ShoppingItemBeanLabelStyle {
            return if (value != null) {
                values().getOrElse(value) { STYLE_NEUTRAL }
            } else {
                STYLE_NEUTRAL
            }
        }
    }
}
