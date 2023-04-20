package com.kamilh.volleyballstats.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChipGroup(
    mainAxisSpacing: Dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {

    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(mainAxisSpacing),
    ) {
        content()
    }
}
