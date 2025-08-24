package com.pine.pinedroid.jetpack.ui

import androidx.compose.runtime.Composable
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
import coil.compose.AsyncImagePainter.State
import coil.compose.DefaultModelEqualityDelegate
import coil.compose.EqualityDelegate
import coil.compose.rememberAsyncImagePainter
import com.pine.pinedroid.R

@Composable
fun PineAsyncImage(
    model: Any?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    placeholder: Painter? = painterResource(R.drawable.pinedroid_image_loading),
    error: Painter? = painterResource(R.drawable.pinedroid_image_off),
    fallback: Painter? = error,
    onLoading: ((State.Loading) -> Unit)? = null,
    onSuccess: ((State.Success) -> Unit)? = null,
    onError: ((State.Error) -> Unit)? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DefaultFilterQuality,
    clipToBounds: Boolean = true,
    modelEqualityDelegate: EqualityDelegate = DefaultModelEqualityDelegate,
){

    AsyncImage(
        model = model,
        contentDescription = contentDescription,
        modifier = modifier,
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