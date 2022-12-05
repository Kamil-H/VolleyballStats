package com.kamilh.volleyballstats.ui.extensions

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*

@Composable
fun LazyListState.snapToIndex(index: Int) {
    val scrollToItem = shouldScrollToItem(index = index)

    LaunchedEffect(scrollToItem) {
        if (scrollToItem) {
            animateScrollToItem(index)
        }
    }
}

@Composable
private fun LazyListState.shouldScrollToItem(index: Int): Boolean {
    val isScrollingUp = isScrollingUp()
    return remember(index) {
        derivedStateOf {
            if (isScrollingUp) {
                isScrollInProgress && layoutInfo.visibleItemsInfo.find { it.index == index - 1 } != null
            } else {
                isScrollInProgress && firstVisibleItemIndex == index
            }
        }
    }.value
}

@Composable
fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}
