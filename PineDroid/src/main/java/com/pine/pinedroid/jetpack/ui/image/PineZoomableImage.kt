package com.pine.pinedroid.jetpack.ui.image

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import com.pine.pinedroid.R
import com.pine.pinedroid.activity.image_pickup.OneImage

@Composable
fun PineZoomableImage(
    image: OneImage,
    modifier: Modifier = Modifier,
    onScaleChanged: ((Float) -> Unit)? = null
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    fun clampOffset(raw: Offset, currentScale: Float): Offset {
        val maxX = (containerSize.width * (currentScale - 1) / 2f).coerceAtLeast(0f)
        val maxY = (containerSize.height * (currentScale - 1) / 2f).coerceAtLeast(0f)
        return Offset(
            raw.x.coerceIn(-maxX, maxX),
            raw.y.coerceIn(-maxY, maxY)
        )
    }

    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        val newScale = (scale * zoomChange).coerceIn(1f, 5f)
        scale = newScale
        onScaleChanged?.invoke(newScale)
        if (newScale > 1f) {
            offset = clampOffset(offset + panChange, newScale)
        } else {
            offset = Offset.Zero
        }
    }

    Box(
        modifier = modifier
            .background(Color.Black)
            .clipToBounds()
            .onSizeChanged { containerSize = it }
            .transformable(state = transformableState)
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        if (scale > 1f) {
                            scale = 1f
                            offset = Offset.Zero
                            onScaleChanged?.invoke(1f)
                        } else {
                            scale = 3f
                            onScaleChanged?.invoke(3f)
                        }
                    }
                )
            }
    ) {
        PineAsyncImage(
            model = image,
            contentDescription = "",
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                ),
            contentScale = ContentScale.Fit
        )
    }
}


@Preview
@Composable
fun PreviewZoomablePineImage() {
    PineZoomableImage(
        image = OneImage.Resource(R.drawable.camera),
        modifier = Modifier.fillMaxSize()
    )
}
