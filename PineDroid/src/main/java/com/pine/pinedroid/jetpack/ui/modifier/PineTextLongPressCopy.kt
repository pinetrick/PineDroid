package com.pine.pinedroid.jetpack.ui.modifier

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import com.pine.pinedroid.system.PineClipboard
import com.pine.pinedroid.utils.toast

fun Modifier.pineLongPressCopy(
    text: String,
    onCopied: (() -> Unit)? = null
): Modifier = composed {

    pointerInput(text) {
        detectTapGestures(
            onLongPress = {
                PineClipboard.copyText(text)
                onCopied?.invoke()
            }
        )
    }
}
