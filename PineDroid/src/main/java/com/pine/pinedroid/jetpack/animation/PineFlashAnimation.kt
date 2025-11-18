package com.pine.pinedroid.jetpack.animation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.repeatable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha

@Composable
fun PineFlashAnimation(isFlashing: Boolean, content: @Composable () -> Unit) {
    val alpha by animateFloatAsState(
        targetValue = if (isFlashing) 0.3f else 1f,
        animationSpec = if (isFlashing) {
            infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 500
                    0.3f at 0
                    1.0f at 500
                    0.3f at 1000
                }
            )
        } else {
            // 当停止闪烁时，平滑过渡到最终状态
            keyframes {
                durationMillis = 300
                0.3f at 0
                1.0f at 300
            }
        }
    )

    Box(
        modifier = Modifier.alpha(alpha)
    ) {
        content()
    }
}