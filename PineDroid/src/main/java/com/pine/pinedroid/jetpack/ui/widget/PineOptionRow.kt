package com.pine.pinedroid.jetpack.ui.widget

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pine.pinedroid.jetpack.ui.font.PineIcon

@Composable
fun PineOptionRow(
    title: String,
    icon: String? = null,
    description: String? = null,
    onClick: (() -> Unit)? = null,
) {
    PineOptionRowExt(
        title = title,
        icon = icon,
        description = description,
        onClick = onClick,
        rightIcon = if (onClick == null) null else "\uf054",
        rightIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
    )


}

@Composable
fun PineOptionRowExt(
    title: String,
    icon: String? = null,
    description: String? = null,
    onClick: (() -> Unit)? = null,
    rightIcon: String? = "\uf054",
    rightIconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick?.invoke() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let { icon ->
            PineIcon(
                text = icon,
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        rightIcon?.let { rightIcon ->
            PineIcon(
                text = rightIcon,
                modifier = Modifier.size(20.dp),
                color = rightIconColor
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
fun PineOptionRow() {
    PineOptionRow(title = "Profile", icon = "\uf279")
}

