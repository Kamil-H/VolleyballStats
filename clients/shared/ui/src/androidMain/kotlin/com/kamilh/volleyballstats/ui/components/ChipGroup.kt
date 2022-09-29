package com.kamilh.volleyballstats.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.google.accompanist.flowlayout.FlowRow

@Composable
actual fun ChipGroup(
    modifier: Modifier,
    mainAxisSpacing: Dp,
    crossAxisSpacing: Dp,
    content: @Composable () -> Unit,
) {

    FlowRow(
        modifier = modifier,
        mainAxisSpacing = mainAxisSpacing,
        crossAxisSpacing = crossAxisSpacing,
    ) {
        content()
    }
}
