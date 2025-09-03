package com.pine.pinedroid.ui.value_drag_control

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ValueDragControl(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f,
    steps: Int = 0,
    title: String = "Adjust Value",
    valueSuffix: String = "",
    quickValues: List<Float> = emptyList(),
    modifier: Modifier = Modifier
) {
    var currentValue by remember(value) { mutableStateOf(value) }
    var isDragging by remember { mutableStateOf(false) }

    // 当外部值变化时更新当前值（如果不是正在拖动）
    LaunchedEffect(value) {
        if (!isDragging) {
            currentValue = value
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // 标题和当前值显示
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${currentValue.toInt()}$valueSuffix",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 滑块控件
        Slider(
            value = currentValue,
            onValueChange = { newValue ->
                currentValue = newValue
                isDragging = true
                onValueChange(newValue)
            },
            onValueChangeFinished = {
                isDragging = false
            },
            valueRange = valueRange,
            steps = steps,
            modifier = Modifier.fillMaxWidth()
        )

        // 快速选择按钮（如果有提供）
        if (quickValues.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                quickValues.forEach { quickValue ->
                    FilterChip(
                        selected = currentValue == quickValue,
                        onClick = {
                            currentValue = quickValue
                            onValueChange(quickValue)
                        },
                        label = { Text("${quickValue.toInt()}$valueSuffix") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
        }
    }
}


@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 800,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun RadiusControl() {
    ValueDragControl(
        value = 500f,
        onValueChange = {  },
        valueRange = 50f..1000f,
        steps = 19,
        title = "Radius Control",
        valueSuffix = "m",
        quickValues = listOf(100f, 250f, 500f, 750f)
    )

}