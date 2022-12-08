package com.kamilh.volleyballstats.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.kamilh.volleyballstats.presentation.features.common.SegmentedControlState
import com.kamilh.volleyballstats.ui.extensions.toDp
import com.kamilh.volleyballstats.ui.theme.Dimens

@Composable
fun SegmentedControl(
    state: SegmentedControlState,
    modifier: Modifier = Modifier,
    onItemSelection: (selectedItemIndex: Int) -> Unit,
) {
    SegmentedControl(
        items = state.items,
        selectedIndex = state.selectedIndex,
        modifier = modifier,
        onItemSelection = onItemSelection,
    )
}

@Composable
@Suppress("UnstableCollections")
fun SegmentedControl(
    items: List<String>,
    modifier: Modifier = Modifier,
    selectedIndex: Int = 0,
    cornerRadius: Dp = Dimens.MarginExtraLarge,
    color: Color = MaterialTheme.colorScheme.primary,
    onItemSelection: (selectedItemIndex: Int) -> Unit,
) {
    var minWidth by remember { mutableStateOf(0) }

    Row(modifier = modifier) {
        items.forEachIndexed { index, item ->
            OutlinedButton(
                modifier = Modifier.adjust(index = index, selectedIndex = selectedIndex)
                    .defaultMinSize(minWidth = minWidth.toDp())
                    .onGloballyPositioned {
                        if (it.size.width > minWidth) {
                            minWidth = it.size.width
                        }
                    },
                onClick = {
                    onItemSelection(index)
                },
                shape = shape(index = index, cornerRadius = cornerRadius, itemsSize = items.size),
                colors = if (selectedIndex == index) {
                    ButtonDefaults.outlinedButtonColors(containerColor = color)
                } else {
                    ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent)
                },
            ) {
                Text(
                    text = item,
                    fontWeight = FontWeight.Normal,
                    color = if (selectedIndex == index) contentColorFor(color) else color,
                )
            }
        }
    }
}

private fun Modifier.adjust(index: Int, selectedIndex: Int): Modifier =
    if (index == 0) {
        offset(0.dp, 0.dp).zIndex(1f)
    } else {
        offset((-1 * index).dp, 0.dp).zIndex(if (selectedIndex == index) 1f else 0f)
    }

private fun shape(index: Int, cornerRadius: Dp, itemsSize: Int): Shape =
    when (index) {
        0 -> RoundedCornerShape(
            topStart = cornerRadius,
            topEnd = 0.dp,
            bottomStart = cornerRadius,
            bottomEnd = 0.dp,
        )
        itemsSize - 1 -> RoundedCornerShape(
            topStart = 0.dp,
            topEnd = cornerRadius,
            bottomStart = 0.dp,
            bottomEnd = cornerRadius,
        )
        else -> RoundedCornerShape(0.dp)
    }

