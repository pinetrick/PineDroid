package com.pine.pinedroid.jetpack.ui.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.pine.pinedroid.utils.ui.pct

@Composable
fun PineGeneralScreen(
    title: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
        tonalElevation = 1.pct
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            title()
            content()
        }


    }


}

@Composable
fun PineGeneralScreen(
    title: @Composable () -> Unit,
    content: @Composable () -> Unit,
    floatingActionButton: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
        tonalElevation = 1.pct
    ) {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                title()
                content()
            }

            Box(
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                floatingActionButton()
            }

        }

    }


}