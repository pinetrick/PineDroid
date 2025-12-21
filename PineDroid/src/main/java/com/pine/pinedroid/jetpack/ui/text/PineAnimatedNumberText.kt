package com.pine.pinedroid.jetpack.ui.text

import androidx.compose.animation.core.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.pine.pinedroid.utils.pineFormat

@Composable
fun PineAnimatedNumberText(
    targetValue: Float?,
    placeholder: String = "Unknown",
    initialValue: Float = 0f,
    durationMillis: Int = 1500,
    precision: Int = 2,
    color: Color = Color.White,
    fontSize: TextUnit = 52.sp,
    fontWeight: FontWeight = FontWeight.ExtraBold,
    letterSpacing: TextUnit = 1.sp,
    prefix: String = "",
    suffix: String = "",
    delayMillis: Int = 0,
    easing: Easing = FastOutSlowInEasing,
    onAnimationComplete: () -> Unit = {}
) {
    // 如果 targetValue 为 null，直接显示占位符
    if (targetValue == null) {
        Text(
            text = placeholder,
            color = color,
            fontSize = fontSize,
            fontWeight = fontWeight,
            letterSpacing = letterSpacing
        )
        return
    }

    // 使用 Animatable 来实现更可控的动画
    val animatable = remember { Animatable(initialValue) }

    // 当 targetValue 变化时，启动动画
    LaunchedEffect(targetValue) {
        animatable.animateTo(
            targetValue = targetValue,
            animationSpec = tween(
                durationMillis = durationMillis,
                delayMillis = delayMillis,
                easing = easing
            )
        )
        onAnimationComplete()
    }

    Text(
        text = "${prefix}${animatable.value.pineFormat(precision)}${suffix}",
        color = color,
        fontSize = fontSize,
        fontWeight = fontWeight,
        letterSpacing = letterSpacing
    )
}

@Composable
fun PineAnimatedNumberText(
    targetValue: Double?,
    placeholder: String = "Unknown",
    initialValue: Double = 0.0,
    durationMillis: Int = 1500,
    precision: Int = 2,
    color: Color = Color.White,
    fontSize: TextUnit = 52.sp,
    fontWeight: FontWeight = FontWeight.ExtraBold,
    letterSpacing: TextUnit = 1.sp,
    prefix: String = "",
    suffix: String = "",
    delayMillis: Int = 0,
    easing: Easing = FastOutSlowInEasing,
    onAnimationComplete: () -> Unit = {}
) {
    PineAnimatedNumberText(
        targetValue = targetValue?.toFloat(),
        placeholder = placeholder,
        initialValue = initialValue.toFloat(),
        durationMillis = durationMillis,
        precision = precision,
        color = color,
        fontSize = fontSize,
        fontWeight = fontWeight,
        letterSpacing = letterSpacing,
        prefix = prefix,
        suffix = suffix,
        delayMillis = delayMillis,
        easing = easing,
        onAnimationComplete = onAnimationComplete
    )
}


