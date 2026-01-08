package com.pine.pinedroid.jetpack.ui.search.no_record

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.pine.pinedroid.R
import com.pine.pinedroid.jetpack.ui.font.PineIcon
import com.pine.pinedroid.utils.ui.pct
import com.pine.pinedroid.utils.ui.spwh

@Composable
fun PineSearchNoResult(
    text: String = stringResource(R.string.pine_search_no_record),
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PineIcon(
            text = "\ue2ca",
            fontSize = 40.spwh,
            modifier = Modifier.padding(bottom = 8.pct),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        Text(
            text,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}


@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 800,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun Preview() {
    PineSearchNoResult()
}