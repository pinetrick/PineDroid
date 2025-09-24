package com.pine.pinedroid.jetpack.ui.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultFilterQuality
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.AsyncImagePainter.State
import coil.compose.DefaultModelEqualityDelegate
import coil.compose.EqualityDelegate
import com.pine.pinedroid.R
import com.pine.pinedroid.activity.image_pickup.OneImage
import java.io.File


import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.pine.pinedroid.activity.image_pickup.toLocalImage


@Composable
fun PineAsyncImage(
    model: Any?,
    useThumbnail: Boolean = false,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    placeholder: Painter? = painterResource(R.drawable.pinedroid_image_loading),
    error: Painter? = painterResource(R.drawable.pinedroid_image_off),
    fallback: Painter? = error,
    onLoading: ((State.Loading) -> Unit)? = null,
    onSuccess: ((State.Success) -> Unit)? = null,
    onError: ((State.Error) -> Unit)? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Crop,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DefaultFilterQuality,
    clipToBounds: Boolean = true,
    modelEqualityDelegate: EqualityDelegate = DefaultModelEqualityDelegate,
    onClicked: ((Any?) -> Unit)? = null,
) {


    val processedModel = when (model) {
        is OneImage.UriImage -> {
            if (useThumbnail) model.thumbnail ?: model.uri
            else model.uri
        }
        is OneImage.HttpImage -> {
            val localImage by produceState<Any?>(initialValue = R.drawable.pinedroid_image_loading, model) {
                value = File( model.toLocalImage().localUrl)
            }
            localImage
        }

        is OneImage.LocalImage -> File(model.localUrl)
        is OneImage.Resource -> model.resourceId
        else -> model // 保持其他类型不变
    }

    val clickableModifier = if (onClicked != null) {
        modifier.clickable(
            indication = null, // 去掉涟漪/背景变化
            interactionSource = remember { MutableInteractionSource() }
        ) {
            onClicked(model)
        }
    } else {
        modifier
    }

    AsyncImage(
        model = processedModel,
        contentDescription = contentDescription,
        modifier = clickableModifier,
        placeholder = placeholder,
        error = error,
        fallback = fallback,
        onLoading = onLoading,
        onSuccess = onSuccess,
        onError = onError,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,
        filterQuality = filterQuality,
        clipToBounds = clipToBounds,
        modelEqualityDelegate = modelEqualityDelegate,
    )
}