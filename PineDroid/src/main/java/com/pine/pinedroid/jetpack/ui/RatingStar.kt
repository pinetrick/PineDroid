package com.pine.pinedroid.jetpack.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun RatingStar(
    rating: Float,
    maxRating: Int = 5,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    Row(modifier = Modifier.fillMaxSize()) {
        for (i in 1..maxRating) {
            Icon(
                imageVector = when {
                    i <= rating -> Icons.Filled.Star
                    i - 0.5f <= rating -> Icons.AutoMirrored.Filled.StarHalf
                    else -> Icons.Outlined.StarBorder
                },
                contentDescription = "",
                tint = color,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            )
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 800,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun RatingStarPreview() {
    RatingStar(2.5f, 5)
}
