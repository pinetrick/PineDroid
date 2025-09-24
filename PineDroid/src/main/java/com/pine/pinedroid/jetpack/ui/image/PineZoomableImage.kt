package com.pine.pinedroid.jetpack.ui.image

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toSize
import com.pine.pinedroid.R
import com.pine.pinedroid.activity.image_pickup.OneImage
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import kotlinx.coroutines.launch

@Composable
fun PineZoomableImage(
    image: OneImage,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()
    var imageSize by remember { mutableStateOf(IntSize.Zero) }
    // 最新双击位置（相对于图片的坐标）
    var lastTapOffset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .background(Color.Black)
            .clipToBounds()
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { tapOffset ->
                        lastTapOffset = tapOffset
                        val targetScale = if (scale.value == 1f) 4f else 1f
                        coroutineScope.launch {
                            scale.animateTo(targetScale, animationSpec = tween(300))
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
                .onSizeChanged { imageSize = it }
                .graphicsLayer(
                    scaleX = scale.value,
                    scaleY = scale.value,
                    transformOrigin = if (imageSize.width != 0 && imageSize.height != 0) {
                        TransformOrigin(
                            pivotFractionX = lastTapOffset.x / imageSize.width,
                            pivotFractionY = lastTapOffset.y / imageSize.height
                        )
                    } else {
                        TransformOrigin.Center
                    }
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
