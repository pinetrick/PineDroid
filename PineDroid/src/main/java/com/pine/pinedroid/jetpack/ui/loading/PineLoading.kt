package com.pine.pinedroid.jetpack.ui.loading

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pine.pinedroid.R
import kotlinx.coroutines.delay


@Composable
fun PineLoading(
    isLoading: Boolean = true,
    title: String? = null,
    subtitle: String? = null,
    content: @Composable () -> Unit = {}
) {
    val isPreview = LocalInspectionMode.current
    val shouldShowLoading = if (isPreview) false else isLoading

    val defaultTitle = stringResource(R.string.pine_loading)
    val cyclingSubtitles = listOf(
        stringResource(R.string.pine_loading_subtitle),
        stringResource(R.string.pine_loading_cycling_1),
        stringResource(R.string.pine_loading_cycling_2),
        stringResource(R.string.pine_loading_cycling_3),
    )

    var subtitleIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(shouldShowLoading) {
        subtitleIndex = 0
        if (!shouldShowLoading || subtitle != null) return@LaunchedEffect
        delay(1000)
        var i = 1
        while (true) {
            subtitleIndex = i % cyclingSubtitles.size
            delay(2000)
            i++
        }
    }

    if (shouldShowLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(64.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 4.dp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = title ?: defaultTitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                AnimatedContent(
                    targetState = subtitle ?: cyclingSubtitles[subtitleIndex],
                    transitionSpec = {
                        fadeIn(animationSpec = tween(400)) togetherWith
                        fadeOut(animationSpec = tween(400))
                    },
                    label = "subtitle"
                ) { text ->
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
            }
        }
    } else content()
}

@Preview
@Composable
fun Preview() {
    PineLoading(
        title = stringResource(R.string.pine_camera_starting),
        subtitle = stringResource(R.string.pine_camera_starting_subtitle)
    )
}
